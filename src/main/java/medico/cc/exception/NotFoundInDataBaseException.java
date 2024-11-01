package medico.cc.exception;

public class NotFoundInDataBaseException extends Exception{
    String e = "No se ha encontrado en la base de datos";
    public NotFoundInDataBaseException(String e){
        this.e = e;
    }

    @Override
    public String getMessage() {
        return e;
    }
}
