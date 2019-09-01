package com.ilearn1234.monitor.util;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * InfluxDB数据库连接操作类
 *
 * @author Happy王子乐
 */
@Slf4j
public class InfluxDBUtil {

    // 用户名
    private String username;
    // 密码
    private String password;
    // 连接地址
    private String openurl;
    // 数据库
    private String database;
    // 保留策略
    private String retentionPolicy;
    // InfluxDB对象
    private InfluxDB influxDB;

    public InfluxDBUtil(String username, String password, String openurl, String database,
                        String retentionPolicy) {
        this.username = username;
        this.password = password;
        this.openurl = openurl;
        this.database = database;
        this.retentionPolicy = retentionPolicy == null || retentionPolicy.equals("") ? "autogen" : retentionPolicy;
        influxDbBuild();
    }

    /**
     * 创建数据库
     *
     * @param dbName 数据库名
     */
    @SuppressWarnings("deprecation")
    public void createDB(String dbName) {
        influxDB.createDatabase(dbName);
    }

    /**
     * 删除数据库
     *
     * @param dbName 数据库名
     */
    @SuppressWarnings("deprecation")
    public void deleteDB(String dbName) {
        influxDB.deleteDatabase(dbName);
    }

    /**
     * 测试连接是否正常
     *
     * @return true 正常
     */
    public boolean ping() {
        boolean isConnected = false;
        Pong pong;
        try {
            pong = influxDB.ping();
            if (pong != null) {
                isConnected = true;
            }
        } catch (Exception e) {
            log.error("InfluxDBUtil ping is error", e);
        }
        return isConnected;
    }

    /**
     * 连接时序数据库 ，若不存在则创建
     *
     * @return 数据库实例
     */
    public InfluxDB influxDbBuild() {
        if (influxDB == null) {
            influxDB = InfluxDBFactory.connect(openurl, username, password);
            influxDB.setDatabase(database);
        }
        influxDB.setLogLevel(InfluxDB.LogLevel.NONE);
        return influxDB;
    }

    /**
     * 创建自定义保留策略
     *
     * @param policyName  策略名
     * @param duration    保存天数
     * @param replication 保存副本数量
     * @param isDefault   是否设为默认保留策略
     */
    public void createRetentionPolicy(String policyName, String duration, int replication, Boolean isDefault) {
        String sql = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s ", policyName,
                database, duration, replication);
        if (isDefault) {
            sql = sql + " DEFAULT";
        }
        this.query(sql);
    }

    /**
     * 创建默认的保留策略
     *
     * @param 策略名：default，保存天数：30天，保存副本数量：1，设为默认保留策略
     */
    public void createDefaultRetentionPolicy() {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
                "default", database, "30d", 1);
        this.query(command);
    }

    /**
     * 查询
     *
     * @param command 查询语句
     * @return 查询结果
     */
    public QueryResult query(String command) {
        return influxDB.query(new Query(command, database));
    }

    /**
     * 插入
     *
     * @param measurement 表
     * @param tags        标签
     * @param fields      字段
     */
    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields, long time,
                       TimeUnit timeUnit) {
        Point.Builder builder = Point.measurement(measurement);
        builder.tag(tags);
        builder.fields(fields);
        if (0 != time) {
            builder.time(time, timeUnit);
        }
        influxDB.write(database, retentionPolicy, builder.build());
    }

    /**
     * 写入测点
     *
     * @param point 单个数据点
     */
    public void insert(Point point) {
        influxDB.write(point);
    }

    /**
     * 批量写入测点
     *
     * @param batchPoints 批量数据点
     */
    public void batchInsert(BatchPoints batchPoints) {
        influxDB.write(batchPoints);
    }

    /**
     * 批量写入数据
     *
     * @param database        数据库
     * @param retentionPolicy 保存策略
     * @param consistency     一致性
     * @param records         要保存的数据（调用BatchPoints.lineProtocol()可得到一条record）
     */
    public void batchInsert(final String database, final String retentionPolicy,
                            final InfluxDB.ConsistencyLevel consistency, final List<String> records) {
        influxDB.write(database, retentionPolicy, consistency, records);
    }

    /**
     * 删除
     *
     * @param command 删除语句
     * @return 返回错误信息
     */
    public String deleteMeasurementData(String command) {
        QueryResult result = influxDB.query(new Query(command, database));
        return result.getError();
    }

    /**
     * 关闭数据库
     */
    public void close() {
        influxDB.close();
    }

    /**
     * 构建Point
     *
     * @param measurement 表
     * @param time        打点时间
     * @param tags        标签
     * @param fields      字段
     * @return 点数据对象
     */
    public Point pointBuilder(String measurement, long time, Map<String, String> tags, Map<String, Object> fields) {
        return Point.measurement(measurement).time(time, TimeUnit.MILLISECONDS).tag(tags).fields(fields).build();
    }
}
