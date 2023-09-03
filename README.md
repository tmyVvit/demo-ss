# Getting Started
一个简单的使用 ShardingSphere 的示例，包含了分库分表、雪花算法生成 ID 等功能。

## 表结构
`list_info` 是一个分表的表，分表字段是 `create_time`，分表规则是按照 `create_time` 的年月进行分表。
```sql
create table list_meta (
	id bigint not null comment 'id',
	list_name varchar(50) not null comment 'list name',
	list_description varchar(200) default null comment '描述',
	encrypt_type varchar(20) default 'plain' comment '加密方式',
	primary key (id)
)engine=innodb charset=utf8mb4 collate=utf8mb4_bin;

create table list_info_202309 (
	id bigint not null,
	list_id bigint not null comment 'list id',
	name varchar(50) not null comment 'name',
	create_user varchar(20) comment '创建人',
	update_user varchar(20) comment '更新人',
	create_time datetime default current_timestamp,
	update_time datetime default current_timestamp on update current_timestamp,
	primary key (id)
)engine=innodb charset=utf8mb4 collate=utf8mb4_bin;

create table list_info_202310 (
	id bigint not null,
	list_id bigint not null comment 'list id',
	name varchar(50) not null comment 'name',
	create_user varchar(20) comment '创建人',
	update_user varchar(20) comment '更新人',
	create_time datetime default current_timestamp,
	update_time datetime default current_timestamp on update current_timestamp,
	primary key (id)
)engine=innodb charset=utf8mb4 collate=utf8mb4_bin;

```

## 配置
两种配置方式，一种是在代码中配置，在 `ShardingDatasourceConfig.shardingDatasource` 中定义；另一种是通过 `yaml` 配置文件。
可以使用 `sharding.load.type` 来控制是使用 `yaml` 配置文件还是代码中配置。