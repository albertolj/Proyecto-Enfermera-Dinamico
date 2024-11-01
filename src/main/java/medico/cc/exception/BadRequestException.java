package medico.cc.exception;

public class BadRequestException extends Exception{
    String e = "";
    public BadRequestException(String e){
        this.e = e;
    }

    @Override
    public String getMessage() {
        return e;
    }
}
