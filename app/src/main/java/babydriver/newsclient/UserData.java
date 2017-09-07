package babydriver.newsclient;

/**
 * Created by KevinBing on 2017/9/7.
 * 用户数据类型的声明
 */

public class UserData
{
    private String userName;                //用户名
    private String userPassword;            //用户密码
    private int userID;                     //用户独一无二的 ID
    public int passwordResetCounter = 0;    //用户更改密码次数，用判断用户异常

    //******************************用户名*******************************
    //获得用户名
    public String getUserName(){
        return userName;
    }
    //设置用户名
    public void setUserName(String name){   //用户输入的用户名
        this.userName = name;
    }

    //*******************************密码********************************
    //获取用户密码
    public String getUserPassword(){
        return  userPassword;
    }
    //设置用户密码
    public void setUserPassword(String password){
        this.userPassword = password;
    }

    //********************************ID*********************************
    //获取用户 ID
    public int getUserID(){
        return userID;
    }
    //设置用户 ID
    public void setUserID(int id){
        this.userID = id;
    }

    //构造函数
    public UserData(String userName, String userPassword){
        super();
        this.userName = userName;
        this.userPassword = userPassword;
    }

}
