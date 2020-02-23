
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


