package modular;
//自定义异常类：RuntimeException 是 Java 标准库自带的内置类
//1. 用户被锁定异常
public class AccountLockedException extends RuntimeException{
    public AccountLockedException(String message){
        super(message);  //把错误信息传给父类存起来,这样 catch 到异常后用 e.getMessage() 就能拿到这条信息
    }
}