/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jvn.JvnObjectImpl.States;



public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
	
private ArrayList<Triplet> listObjLocalServer;
private HashSet<Triplet> reader;
private Triplet writer;




/**
  * Default constructor
  * @throws JvnException
  **/
	private JvnCoordImpl() throws Exception {
             this.listObjLocalServer = new ArrayList<>();
             this.writer = null;
             this.reader = new HashSet<Triplet>();
	}

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  public int jvnGetObjectId()
  throws java.rmi.RemoteException,jvn.JvnException {
      int joi=0;
      joi++;
    // to be compl
    return joi;
  }
  
  /**
  * Associate a symbolic name with a JVN object
  * @param jon : the JVN object name
  * @param jo  : the JVN object 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
      Triplet t = new Triplet(jon, jo, js);
      this.listObjLocalServer.add(t);
      
  }

    public ArrayList<Triplet> getListObjLocalServer() {
        return listObjLocalServer;
    }
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
	  
	  JvnObject object = null;
	   for(Triplet t:this.listObjLocalServer) {
		   if(t.getName().equals(jon) && t.getJvnRemoteServer().equals(js)) {
			   object = t.getJvnObject();
		   }
	   }
	    return object;
  }
  
  /**
  * Get a Read lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockRead(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
   
       Serializable result=null;
       for(Triplet triplet : listObjLocalServer){
           result = triplet.getJvnObject().jvnGetObjectState();
           
           //case2
           if(triplet.getJvnObject().jvnGetObjectId()==joi && triplet.getJvnRemoteServer().equals(js)){
            //change d'etat dans le coord
          triplet.getJvnObject().setEtatVerrou(JvnObjectImpl.States.R);
            return result;
        }
           //case3
         if ((triplet.getJvnObject().jvnGetObjectId()==joi) && !(triplet.getJvnRemoteServer().equals(js))){
         if (triplet.getJvnObject().getEtatVerrou().equals(States.R)) {
                listObjLocalServer.add(new Triplet("IRC", triplet.getJvnObject(), js));
                return result;
            }
        }
         
         //case 6
        if ((triplet.getJvnObject().jvnGetObjectId()==joi) && !(triplet.getJvnRemoteServer().equals(js))){
          if (triplet.getJvnObject().getEtatVerrou().equals(States.W)) {

              triplet.getJvnRemoteServer().jvnInvalidateWriterForReader(joi);
            // change d'etat dans le coord
              triplet.getJvnObject().setEtatVerrou(JvnObjectImpl.States.R);
              //add le nouveau Server Local qui lit l'object
            listObjLocalServer.add(new Triplet("IRC", triplet.getJvnObject(), js));
              return result;
            
            }
            
        }
   }
    return null;
    
   }
       
       
       /*
	   Serializable result = null;
	   for (Triplet triplet : listObjLocalServer) {
		   try {
			   if(triplet.getJvnObject().jvnGetObjectId() == joi && triplet.getJvnRemoteServer().equals(js)) {
				   if(this.writer == null) {
					   result = triplet.getJvnObject().jvnGetObjectState();
				   }else {
					   result = this.writer.getJvnRemoteServer().jvnInvalidateWriterForReader(this.writer.getJvnObject().jvnGetObjectId());
					   this.reader.add(this.writer);
				   }
				   this.reader.add(triplet);
			   }
		   }catch(Exception e) {
			   System.err.println("Error on server at jvnLockRead():" + e) ;
				e.printStackTrace();
		   }
	   }
    
    return result;
   }
       }
   /*

  /**
  * Get a Write lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
       
     Serializable result=null;
       for(Triplet triplet : listObjLocalServer){
           result = triplet.getJvnObject().jvnGetObjectState();
           
          if(triplet.getJvnObject().jvnGetObjectId()==joi && triplet.getJvnRemoteServer().equals(js)){
            //change d'etat dans le coord
          triplet.getJvnObject().setEtatVerrou(JvnObjectImpl.States.W);
            return result;
        }
/*           
            //case 7
if ((triplet.getJvnObject().jvnGetObjectId()==joi) && !(triplet.getJvnRemoteServer().equals(js))){
            if (triplet.getJvnObject().getEtatVerrou().equals(States.W)) {
            triplet.getJvnRemoteServer().jvnInvalidateWriter(joi);
            //change le nouveau server local qui va ecrire
            triplet.setJvnRemoteServer(js);
            
            return result;     
            }
        }

            //case 8
if ((triplet.getJvnObject().jvnGetObjectId()==joi) && !(triplet.getJvnRemoteServer().equals(js))){
            if (triplet.getJvnObject().getEtatVerrou().equals(States.R)) {
            triplet.getJvnRemoteServer().jvnInvalidateReader(joi);
            triplet.getJvnObject().setEtatVerrou(States.W);
            //change le nouveau server local qui va ecrire
            triplet.setJvnRemoteServer(js);
            return result;     
            }   
 }

    */   }
       
       
       
    return null;
       
   }
	/*   Serializable result = null;
	   for (Triplet triplet : listObjLocalServer) {
		   try {
			   if(triplet.getJvnObject().jvnGetObjectId() == joi && triplet.getJvnRemoteServer().equals(js)) {
				   if(this.writer == null) {
					   result = triplet.getJvnObject().jvnGetObjectState();
					   if(!this.reader.isEmpty()) {
						   for(Triplet t:this.reader) {
							   t.getJvnRemoteServer().jvnInvalidateReader(t.getJvnObject().jvnGetObjectId());
							   this.reader.remove(t);
						   }
					   }
				   }else {
					   result = this.writer.getJvnRemoteServer().jvnInvalidateWriter(this.writer.getJvnObject().jvnGetObjectId());
				   }
				   this.writer = triplet;
				   
			   }
		   }catch(Exception e) {
			   System.err.println("Error on server at jvnLockRead():" + e) ;
				e.printStackTrace();
		   }
	   }
    
    return result;
   }
*/
   
	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
    	for(Triplet t:this.listObjLocalServer) {
    		if(t.getJvnRemoteServer().equals(js)) {
    			this.listObjLocalServer.remove(t);
    		}
    	}
    }
    
    
    public static void main(String[] args) {
    	JvnRemoteCoord h_stub;
		try {
			h_stub = new JvnCoordImpl();
			// Registry registry= LocateRegistry.getRegistry();
                        Registry registry= LocateRegistry.getRegistry(40000);
			 registry.rebind("Coord", h_stub);
			 System.out.println ("Server ready");
		} catch (Exception e)  {
			System.err.println("Error on server :" + e) ;
			e.printStackTrace();
		}

	}
}

 
