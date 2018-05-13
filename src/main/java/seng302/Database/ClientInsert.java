package seng302.Database;

import seng302.Client;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ClientInsert {


    public void databaseAddClient(Client client){

        SessionFactory factory;
        try{
            factory = new Configuration().configure("seng302/Database").buildSessionFactory();
        } catch (Throwable ex){
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);

        }
        Session session = factory.openSession();
        Transaction tx = null;

        try{
            tx = session.beginTransaction();
            session.save(client);
            tx.commit();
        } catch (HibernateException e){
            if(tx!=null) tx.rollback();
            e.printStackTrace();

        } finally {
            session.close();
        }

    }

}
