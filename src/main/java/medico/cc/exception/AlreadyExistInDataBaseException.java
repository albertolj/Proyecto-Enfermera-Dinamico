package medico.cc.exception;

public class AlreadyExistInDataBaseException extends Exception{
    String e = "";
    public AlreadyExistInDataBaseException(String e){
        this.e = e;
    }

    @Override
    public String getMessage(){
        return e;
    }
    
}
