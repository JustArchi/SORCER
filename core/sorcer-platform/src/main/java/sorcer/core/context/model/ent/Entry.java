package sorcer.core.context.model.ent;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sorcer.core.context.ContextSelection;
import sorcer.core.context.ServiceContext;
import sorcer.service.*;
import sorcer.service.modeling.*;
import sorcer.util.bdb.objects.UuidObject;
import sorcer.util.url.sos.SdbUtil;

import java.net.URL;
import java.rmi.RemoteException;

public class Entry<V> extends Association<String, V>
        implements Identifiable, Evaluation<V>, Callable<V>, Setter, Reactive<V>, ent<V> {

    private static final long serialVersionUID = 1L;

    protected static Logger logger = LoggerFactory.getLogger(Entry.class.getName());

    protected Uuid id = UuidFactory.generate();

    protected boolean negative;

    // if a value is computed then isCached is true - computed only one for all
    protected boolean isCached = false;

    // its arguments is persisted
    protected boolean isPersistent = false;

    // if reactive then its values are evaluated if active (either Evaluation or Invocation type)
    protected boolean isReactive = false;

    protected ContextSelection contextSelector;

    public Entry() {
    }

    public Entry(String key) {
        this.key = key;
    }

    public Entry(String key, Object item) {
       super(key, item);
        if (sorcer.util.url.sos.SdbUtil.isSosURL(item)) {
            isPersistent = true;
        }
        if (item != null && item.getClass().getName().indexOf("Lambda") > 0) {
            type = Functionality.Type.LAMBDA;
        } else {
            type = Functionality.Type.ENT;
        }
    }

    @Override
    public Uuid getId() {
        return id;
    }

    public V getOut() {
        return out;
    }

    public void setOut(V out) {
        this.out = out;
    }

    public Object getImpl() {
        if (!isValid && multiFi != null) {
            Object select = multiFi.getSelect();
            if (select instanceof Entry) {
                Object selectImpl = ((Entry) multiFi.getSelect()).getImpl();
                if (selectImpl != null) {
                    impl = ((Entry) multiFi.getSelect()).getImpl();
                }
            } else {
                impl = (multiFi.getSelect());
            }
        }
        isValid = true;
        return impl;
    }

    public Object applyFidelity() {
        if (multiFi != null && multiFi.isChanged()) {
            impl = multiFi.getSelect();;
        }
        return impl;
    }

    @Override
    public void setValue(Object value) throws SetterException, RemoteException {
        if (isPersistent) {
            try {
                if (SdbUtil.isSosURL(value)) {
                    this.out = (V) value;
                    this.impl = value;
                } else if (SdbUtil.isSosURL(this.impl)) {
                    if (((URL) this.impl).getRef() == null) {
                        this.impl = (V) SdbUtil.store(value);
                    } else {
                        SdbUtil.update((URL) this.impl, value);
                    }
                }
            } catch (MogramException | SignatureException e) {
                throw new SetterException(e);
            }
        } else {
            this.out = (V) value;
        }
    }

    public void setContextSelector(ContextSelection contextSelector) {
        this.contextSelector = contextSelector;
    }

    public V getData(Arg... args) throws ContextException {
        if (out != null) {
            if (multiFi == null && isValid) {
                return out;
            } else if (!multiFi.isChanged() && isValid) {
                return out;
            }
        } else {
            out = get(args);
            isValid = true;
        }
        return out;
    }

    @Override
    public V get(Arg... args) throws ContextException {
        if (multiFi != null && multiFi.isChanged()) {
            impl = multiFi.getSelect();
            multiFi.setChanged(false);
        }
        if (impl instanceof Entry && ((Entry) impl).getKey().equals(key)) {
            out = (V) ((Entry) impl).getData(args);
            isValid = true;
            return out;
        }
        Object val = impl;
        URL url = null;
        try {
            substitute(args);
            if (isPersistent) {
                if (SdbUtil.isSosURL(val)) {
                    val = (V) ((URL) val).getContent();
                    if (val instanceof UuidObject)
                        val = (V) ((UuidObject) val).getObject();
                } else {
                    if (val instanceof UuidObject) {
                        url = SdbUtil.store(val);
                    } else {
                        UuidObject uo = new UuidObject(val);
                        uo.setName(key);
                        url = SdbUtil.store(uo);
                    }
                    impl = url;
                    out = null;
                }
                return (V) val;
            } else if (val instanceof Invocation) {
                Context cxt = (Context) Arg.selectDomain(args);
                out = (V) ((Invocation) val).invoke(cxt, args);
            } else if (val instanceof Evaluation) {
                if (val instanceof Entry && ((Entry)val).getName().equals(key)) {
                    out = (V) ((Entry)val).get(args);
                } else {
                    out = ((Evaluation<V>) val).evaluate(args);
                }
            } else if (val instanceof Valuation) {
                out = (V) ((Valuation) val).valuate();
            } else if (val instanceof Ref) {
                Object deref = ((Ref)val).get();
                if (deref instanceof  Evaluation) {
                    if (deref instanceof Scopable) {
                        ((Scopable)deref).setScope(((Ref)val).getScope());
                    }
                    out = (V) ((Evaluation)deref).evaluate(args);
                } else {
                    out = (V) ((Entry)deref).get(args);
                }
            } else if (val instanceof ServiceFidelity) {
                // return the selected fidelity of this entry
                for (Arg arg : args) {
                    if (arg instanceof Fidelity) {
                        if (((Fidelity)arg).getPath().equals(key)) {
                            ((ServiceFidelity)val).selectSelect(arg.getName());
                            break;
                        }
                    }
                }
                out = (V) ((Entry)((ServiceFidelity) val).getSelect()).get(args);
            } else if (val instanceof Callable) {
                out = (V) ((Callable)val).call(args);
            } else if (val instanceof Service) {
                out = (V) ((Service)val).execute(args);
            } else
                // impl is just the out
                // it is recommended to set out and impl to the same valuate
                // when the impl is implementation of the out valuate
                if (out == null && impl != null) {
                    out = (V) impl;
                }
        } catch (Exception e) {
            throw new ContextException(e);
        }
        if (contextSelector != null && out instanceof Context) {
            try {
                out = (V) contextSelector.doSelect(val);
            } catch (ContextException e) {
                throw new ContextException(e);
            }
        }
        if (out instanceof Number && negative) {
            Number result = (Number) val;
            Double rd = result.doubleValue() * -1;
            out = (V) rd;
        }
        return (V) out;
    }

    @Override
    public boolean isReactive() {
        return isReactive;
    }

    public Entry<V> setReactive(boolean isReactive) {
        this.isReactive = isReactive;
        return this;
    }

    public boolean isPersistent() {
        return isPersistent;
    }

    public void setPersistent(boolean isPersistent) {
        this.isPersistent = isPersistent;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }


    @Override
    public Entry act(Arg... args) throws ServiceException, RemoteException {
        Object result = this.execute(args);
        if (result instanceof Entry) {
            return (Entry)result;
        } else {
            return new Entry(key, result);
        }
    }

    @Override
    public Data act(String entryName, Arg... args) throws ServiceException, RemoteException {
        Object result = this.execute(args);
        if (result instanceof Entry) {
            return (Entry)result;
        } else {
            return new Entry(entryName, result);
        }
    }

    public Object execute(Arg... args) throws ServiceException, RemoteException {
        Domain cxt = Arg.selectDomain(args);
        if (cxt != null) {
            // entry substitution
            ((ServiceContext)cxt).putValue(key, impl);
            return cxt;
        } else {
            return impl;
        }
    }

    @Override
    public V call(Arg... args) throws EvaluationException, RemoteException {
        try {
            return getData(args);
        } catch (ContextException e) {
            throw new EvaluationException(e);
        }
    }

    public void substitute(Arg... entries) throws SetterException {
        if (entries != null) {
            for (Arg a : entries) {
                if (a instanceof ContextSelection) {
                    setContextSelector((ContextSelection) a);
                }
            }
        }
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
    }

    @Override
    public String getName() {
        return key;
    }


    public String getKey() {
        return key;
    }

    public V asis() {
        if (impl != null && impl instanceof Entry) {
            return (V) ((Entry) impl).asis();
        } else {
            return (V) impl;
        }
    }

    @Override
    public V evaluate(Arg... args) throws EvaluationException, RemoteException {
        try {
            return getData(args);
        } catch (ContextException e) {
            throw new EvaluationException(e);
        }
    }
}
