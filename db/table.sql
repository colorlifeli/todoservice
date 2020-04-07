
create sequence if not exists id_seq start with 100000 cache 1;
comment on sequence id_seq is '为所有表生成主键id';

create table todo (
id int,
content varchar(1000),
type varchar(2),
status varchar(2),
inserttime date,
operatetime date,
finishtime date,
PRIMARY KEY(id)
);
comment on table todo is '待办事项';
comment on column todo.content is '待办内容';
comment on column todo.type is '类型';
comment on column todo.status is '状态';
comment on column todo.inserttime is '创建时间';
comment on column todo.operatetime is '操作时间';
comment on column todo.finishtime is '完成时间';

create table article (
id int,
title varchar(100),
content varchar(10000),
path varchar(100),
tags varchar(100),
folderId int,
status varchar(2),
inserttime date,
operatetime date,
deletetime date,
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
comment on column article.status is '状态，未使用';

create table folder (
id int,
title varchar(100),
parentId int,
leaf varchar(2),
status varchar(2),
inserttime date,
operatetime date,
deletetime date,
PRIMARY KEY(id)
);
comment on table folder is '文件夹';
comment on column folder.id is '主键';
comment on column folder.title is '标题';
comment on column article.parentId is '父文件夹id';
comment on column folder.inserttime is '创建时间';
comment on column folder.operatetime is '更新时间';
comment on column folder.deletetime is '删除时间';
comment on column folder.leaf is '1:叶节点 0:非叶节点';
comment on column folder.status is '状态，未使用';

create table timeline (
id int,
content varchar(10000),
status varchar(2),
inserttime date,
operatetime date,
PRIMARY KEY(id)
);
comment on table timeline is '时间线，日记';
comment on column timeline.id is '主键';
comment on column timeline.content is '内容';
comment on column timeline.inserttime is '创建时间';
comment on column timeline.operatetime is '更新时间';
comment on column folder.deletetime is '删除时间';
comment on column timeline.status is '状态，未使用';
