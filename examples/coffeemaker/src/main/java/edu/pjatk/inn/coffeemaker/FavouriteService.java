package edu.pjatk.inn.coffeemaker;

import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;

public interface FavouriteService {

    public Context addFavourite(Context context) throws RemoteException, ContextException;
    public Context removeFavourite(Context context) throws RemoteException, ContextException;
    public Context getFavourites(Context context) throws RemoteException, ContextException;

}
