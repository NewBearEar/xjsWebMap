package javasrc;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

public class MongoDBUtil extends DatabaseUtil {
    public static MongoDatabase getConnect(String dbname){
        //连接到 mongodb 服务
        MongoClient mongoClient = new MongoClient("123.207.202.25", 27017);

        //连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbname);

        //返回连接数据库对象
        return mongoDatabase;
    }
    public static MongoDatabase getConnectWithAuth(String username,String password,String dbname){
        List<ServerAddress> adds = new ArrayList<>();
        //ServerAddress()两个参数分别为 服务器地址 和 端口
        ServerAddress serverAddress = new ServerAddress("123.207.202.25", 27017);
        adds.add(serverAddress);

        List<MongoCredential> credentials = new ArrayList<>();
        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(username, dbname, password.toCharArray());
        credentials.add(mongoCredential);

        //通过连接认证获取MongoDB连接
        MongoClient mongoClient = new MongoClient(adds, credentials);

        //连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbname);

        //返回连接数据库对象
        return mongoDatabase;

    }
}
