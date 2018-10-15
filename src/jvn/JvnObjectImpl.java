package jvn;

import java.io.Serializable;

import irc.Sentence;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JvnObjectImpl implements JvnObject {

    public Serializable obj;

	public int joi;

    enum States {
        NL, RC, WC, R, W, RWC
    }

    States etatVerrou;

    public JvnObjectImpl(int id, Serializable obj) {
        this.obj = obj;
        this.joi = id;
        this.etatVerrou = States.NL;
    }

    public void jvnLockRead() throws JvnException {

        switch (etatVerrou) {
            case NL:
                obj = JvnServerImpl.jvnGetServer().jvnLockRead(joi);
                etatVerrou = States.R;
                break;

            case RC:
                etatVerrou = States.R;
                break;
            case WC:
                etatVerrou = States.RWC;
                break;

        }
    }

    public void jvnLockWrite() throws JvnException {

        switch (etatVerrou) {
            case WC:
                etatVerrou = States.W;
                break;
            case NL:
                obj = JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
                etatVerrou = States.W;
                break;
            case RC:
                obj = JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
                etatVerrou = States.W;
                break;
        }

    }

    public synchronized void jvnUnLock() throws JvnException {


            switch (etatVerrou) {
                case R:
                    etatVerrou = States.RC;
                    break;
                case W:
                    etatVerrou = States.WC;
                    break;
                case RWC:
                    etatVerrou = States.WC;
                    break;
            }
            notifyAll();

        }
    //}

    public int jvnGetObjectId() throws JvnException {
        return joi;
    }

    public Serializable jvnGetObjectState() throws JvnException {
        return obj;
    }
    
    public void setObjectState(Serializable obj) {
		this.obj = obj;
	}

    public synchronized void jvnInvalidateReader() throws JvnException {

    	try {
            switch (etatVerrou) {
            case RC:
                etatVerrou = States.NL;
                break;
            case RWC:
                etatVerrou = States.NL;
                break;
            case WC:
                etatVerrou = States.NL;
                break;
            case R: 
            	wait();
            	etatVerrou = States.NL;
            	break;
           
            }
        } catch (InterruptedException ex) {
            System.out.println("jvn.JvnObjectImpl.jvnInvalidateReader()");
        }
        
    }

    public synchronized Serializable jvnInvalidateWriter() throws JvnException {

    	try {
    		
    		switch (etatVerrou) {
	    	case W:
	    		wait();
	            etatVerrou = States.NL;
	            break;
            case WC:
                etatVerrou = States.NL;
                break;
            case RC:
                etatVerrou = States.NL;
                break;
            case RWC:
            	wait();
                etatVerrou = States.NL;
                break;
	
    		}
    	} catch (InterruptedException ex) {
            System.out.println("jvn.JvnObjectImpl.jvnInvalidateWriter()");
            Logger.getLogger(JvnObjectImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
            
       
        return obj;
    }

    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        
            switch (etatVerrou) {
                case W:
                	try {
    					wait();
    					etatVerrou = States.RC;
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
                	break;
                case WC:
                    etatVerrou = States.RC;
                    break;

                case RWC:
                	try {
    					wait();
    					etatVerrou = States.RC;
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
            }

        return obj;
    }

}
