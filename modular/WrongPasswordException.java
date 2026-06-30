package modular;
//密码错误异常
public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String message){
        super(message);
    }
}