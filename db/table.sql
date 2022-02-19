
create sequence if not exists id_seq start with 100000 cache 1;
comment on sequence id_seq is '为所有表生成主键id';

create table todo (
id int,
content varchar(1000),
type varchar(2),
status varchar(2),
priority int,
usercode varchar(30),
inserttime timestamp,
operatetime timestamp,
finishtime timestamp,
starttime timestamp,
PRIMARY KEY(id)
);
comment on table todo is '待办事项';
comment on column todo.content is '待办内容';
comment on column todo.type is '类型';
comment on column todo.status is '状态';
comment on column todo.priority is '优先级。1：重要 2：一般 3：次要';
comment on column todo.inserttime is '创建时间';
comment on column todo.operatetime is '更新时间';
comment on column todo.finishtime is '完成时间';
comment on column todo.starttime is '开妈时间';

create table article (
id int,
title varchar(100),
content varchar(10000),
path varchar(100),
tags varchar(100),
folderId int,
status varchar(2),
usercode varchar(30),
inserttime timestamp,
operatetime timestamp,
deletetime timestamp,
PRIMARY KEY(id)
);
comment on table article is '笔记';
comment on column article.id is '主键';
comment on column article.title is '标题';
comment on column article.content is '内容';
comment on column article.path is '文件夹全路径';
comment on column article.tags is '标签';
comment on column article.folderId is '所在文件夹id';
comment on column article.inserttime is '创建时间';
comment on column article.operatetime is '更新时间';
comment on column article.deletetime is '删除时间';
comment on column article.status is '状态，0-正常 2-删除';

create table folder (
id int,
title varchar(100),
parentId int,
leaf varchar(2),
status varchar(2),
usercode varchar(30),
inserttime timestamp,
operatetime timestamp,
deletetime timestamp,
PRIMARY KEY(id)
);
comment on table folder is '文件夹';
comment on column folder.id is '主键';
comment on column folder.title is '标题';
comment on column folder.parentId is '父文件夹id';
comment on column folder.inserttime is '创建时间';
comment on column folder.operatetime is '更新时间';
comment on column folder.deletetime is '删除时间';
comment on column folder.leaf is '1:叶节点 0:非叶节点';
comment on column folder.status is '状态，未使用';

create table timeline (
id int,
content varchar(10000),
status varchar(2),
inserttime timestamp,
operatetime timestamp,
PRIMARY KEY(id)
);
comment on table timeline is '时间线，日记';
comment on column timeline.id is '主键';
comment on column timeline.content is '内容';
comment on column timeline.inserttime is '创建时间';
comment on column timeline.operatetime is '更新时间';
comment on column timeline.status is '状态，未使用';

create table config (
code varchar(30),
name varchar(30),
value varchar(50),
operatetime timestamp,
PRIMARY KEY(code)
);
comment on table config is '系统配置表';
comment on column config.code is '主键,配置代码';
comment on column config.name is '配置描述';
comment on column config.value is '配置值';
comment on column config.operatetime is '更新时间';

insert into config(code,name,value,operatetime)
values('pagesize','文章每页大小','10', CURRENT_TIMESTAMP(0));

create table user (
code varchar(30),
name varchar(30),
pw varchar(30),
role varchar(10),
operatetime timestamp,
PRIMARY KEY(code)
);
comment on table user is '用户表';
comment on column user.code is '主键,用户代码';
comment on column user.name is '用户名称';
comment on column user.pw is '密码';
comment on column user.role is '角色';
comment on column user.operatetime is '更新时间';

