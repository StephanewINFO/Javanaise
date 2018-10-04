package jvn;

import java.io.Serializable;

import irc.Sentence;


public class JvnObjectImpl implements JvnObject{

        
	public Serializable obj;
	public int joi;
	enum States 
        {
		NL,RC, WC,R,W,RWC
	}
        
        States etatVerrou;
	
	public JvnObjectImpl(Serializable obj){
		this.obj=obj;
		joi=obj.hashCode();
		
	}
	
	public void jvnLockRead() throws JvnException {
              JvnServerImpl js = JvnServerImpl.jvnGetServer();
              
              switch(etatVerrou){
                  case NL:
                	  obj = js.jvnLockRead(joi);    
                	  etatVerrou = States.R;
                      break;
     
                  case RC:
                      etatVerrou = States.R;
                      break;
                  case WC:
                	  etatVerrou = States.RWC;
                      break;
                  case W:
                	  etatVerrou = States.W;
                	  break;
                	  
                  default:
                	  etatVerrou = States.R;
                	  break;
                	  
                  
              }
              System.out.println("jvn.jvnObjectImpl.jvnLockRead()");     
              
              
            
                   
            
        }
		// TODO Auto-generated method stub
		
	

	public void jvnLockWrite() throws JvnException {
		JvnServerImpl js = JvnServerImpl.jvnGetServer();
		switch(etatVerrou) {
		case WC:
			etatVerrou = States.W;
			break;
			
		default:
			obj = js.jvnLockRead(joi);
			etatVerrou = States.W;
			break;
		}
		
	}

	public void jvnUnLock() throws JvnException {
		JvnServerImpl js = JvnServerImpl.jvnGetServer();
		switch(etatVerrou) {
		case R:
			etatVerrou = States.RC;
			break;
		case W:
			etatVerrou = States.WC;
		case RWC:
			etatVerrou = States.WC;
		default:
			break;
		}
		
	}

	public int jvnGetObjectId() throws JvnException {
		// TODO Auto-generated method stub
		return 0;
	}
//completar
	public Serializable jvnGetObjectState() throws JvnException {
		return this.obj;
	}


	public void jvnInvalidateReader() throws JvnException {
		etatVerrou = States.NL;
	}

	public Serializable jvnInvalidateWriter() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

       
        
}
