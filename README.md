# Dson

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
[![Java Version](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.org/)

Dson 是一个高性能的 Java JSON 序列化/反序列化库，支持运行时字节码编译优化。

## 特性

- **双模式支持**：标准反射模式和编译模式
- **运行时字节码生成**：编译模式下动态生成字节码，避免反射开销
- **零依赖**：核心功能无第三方依赖
- **注解驱动**：通过注解灵活控制序列化行为
- **自定义转换器**：支持自定义类型的序列化/反序列化逻辑

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>cc.jfire</groupId>
    <artifactId>Dson</artifactId>
    <version>1.0</version>
</dependency>
```

### 基本用法

```java
// 定义实体类
public class Person {
    private String name;
    private int age;
    // getters and setters...
}

Person person = new Person();
person.setName("张三");
person.setAge(30);

// 序列化
String json = Dson.toJson(person);
// 输出: {"name":"张三","age":30}

// 反序列化
Person result = Dson.fromString(Person.class, json);
```

### 编译模式（高性能）

```java
// 使用编译模式序列化
String json = Dson.toJsonByCompile(person);

// 使用编译模式反序列化
Person result = Dson.fromStringByCompile(Person.class, json);
```

### 自定义配置

```java
DsonConfig config = new DsonConfig()
        .setWriteUseCompile(true)   // 序列化使用编译模式
        .setReadUseCompile(true);   // 反序列化使用编译模式

DsonContext context = new DsonContext(config);

String json = context.toJson(person);
Person result = context.fromString(Person.class, json);
```

## 注解

### @JsonIgnore

排除字段，不参与序列化/反序列化：

```java
public class User {
    private String username;

    @JsonIgnore
    private String password; // 此字段将被忽略
}
```

### @JsonRename

自定义字段名称：

```java
public class Product {
    @JsonRename("product_id")
    private String productId;

    private String name;
}
// 输出: {"product_id":"xxx","name":"xxx"}
```

### @JsonRename + JsonRenameStrategy

类级别的命名策略（如驼峰转下划线）：

```java
@JsonRename(strategy = HumpToUnderline.class)
public class UserInfo {
    private String userName;    // -> user_name
    private String phoneNumber; // -> phone_number
}
```

### @SerializeDefinition

指定字段的自定义序列化器：

```java
public class BooleanAsIntWriter implements TypeWriter {
    @Override
    public void toJson(Object entity, StringBuilder output) {
        output.append(((Boolean) entity) ? "1" : "0");
    }
}

public class Settings {
    @SerializeDefinition(BooleanAsIntWriter.class)
    private boolean enabled; // 序列化为 1 或 0
}
```

### @DeSerializeDefinition

指定字段的自定义反序列化器：

```java
public class BooleanAsIntReader implements TypeReader {
    @Override
    public Object fromString(Stream stream) {
        return stream.getInt() == 1;
    }
}

public class Settings {
    @DeSerializeDefinition(BooleanAsIntReader.class)
    private boolean enabled; // 从 1 或 0 反序列化
}
```

## 自定义转换器

### TypeWriter（序列化）

```java
public class CustomDateWriter implements TypeWriter {
    @Override
    public void toJson(Object entity, StringBuilder output) {
        Date date = (Date) entity;
        output.append(date.getTime());
    }
}
```

### TypeReader（反序列化）

```java
public class CustomDateReader implements TypeReader {
    @Override
    public Object fromString(Stream stream) {
        long timestamp = stream.getLong();
        return new Date(timestamp);
    }
}
```

## 支持的类型

- 基本类型：`int`, `long`, `float`, `double`, `boolean`, `byte`, `short`, `char`
- 包装类型：`Integer`, `Long`, `Float`, `Double`, `Boolean`, `Byte`, `Short`, `Character`
- 字符串：`String`
- 日期：`Date`, `java.sql.Date`
- 数组：基本类型数组、对象数组、多维数组
- 集合：`List`, `ArrayList`, `Collection`
- Map：`Map`, `HashMap`
- 枚举：`Enum`
- 嵌套对象
- Record 类型（Java 14+）

## 高级用法

### 泛型类型处理

```java
// 使用 TypeUtil 处理泛型
ArrayList<Person> list = context.fromString(
    new TypeUtil<ArrayList<Person>>(){}.getType(),
    json
);

HashMap<String, Person> map = context.fromString(
    new TypeUtil<HashMap<String, Person>>(){}.getType(),
    json
);
```

### 提取指定属性

```java
String json = "{\"name\":\"张三\",\"data\":{...}}";

// 只提取 data 属性
MyData data = (MyData) Dson.fromStringByAttribute("data", MyData.class, json);
```

## 配置选项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `readUseCompile` | 反序列化使用编译模式 | false |
| `writeUseCompile` | 序列化使用编译模式 | false |
| `readEntryUseCompile` | 读取条目使用编译模式 | false |
| `valueAccessorUseCompile` | 值访问器使用编译模式 | false |

## 许可证

[GNU Affero General Public License v3.0](https://www.gnu.org/licenses/agpl-3.0.txt)

## 作者

- jfirer (495561397@qq.com)

## 链接

- GitHub: https://github.com/linbin-eric/Dson
