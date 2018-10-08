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
	
	public JvnObjectImpl(int joi,Serializable obj){
		this.obj=obj;
		this.joi=joi;
		
	}



    public void setEtatVerrou(States etatVerrou) {
        this.etatVerrou = etatVerrou;
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
           /*     case W:
                	  etatVerrou = States.W;
                	  break;
     /*           	  
                  default:
                	  etatVerrou = States.R;
                	  break;
                	 */ 
                  
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
			obj = js.jvnLockWrite(joi);
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
			etatVerrou = States.NL;
		}
		
	}

	public int jvnGetObjectId() throws JvnException {
		// TODO Auto-generated method stub
		return this.joi;
	}
//No implementado
	public Serializable jvnGetObjectState() throws JvnException {
		return this.obj;
	}

    public States getEtatVerrou() {
        return etatVerrou;
    }


	public void jvnInvalidateReader() throws JvnException {
		
		switch(this.etatVerrou) {
		case RC:
			this.etatVerrou = States.NL;
			break;
		case RWC:
			try {
				wait();
				this.etatVerrou = States.NL;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
}
	}

	public Serializable jvnInvalidateWriter() throws JvnException {
		switch(this.etatVerrou) {
		case WC:
			this.etatVerrou = States.NL;
			break;
		case W:
		case RWC:
			try {
				wait();
				this.etatVerrou = States.NL;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
		return obj;
	}

	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		switch(this.etatVerrou) {
		case WC:
			this.etatVerrou = States.RC;
			break;
		case W:
			try {
				wait();
				this.etatVerrou = States.RC;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case RWC:
			try {
				wait();
				this.etatVerrou = States.R;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			return obj;
		}

       
        
}