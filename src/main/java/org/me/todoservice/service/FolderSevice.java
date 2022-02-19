package org.me.todoservice.service;

import org.me.todoservice.dao.FolderMapper;
import org.me.todoservice.schema.Folder;
import org.me.todoservice.utils.mybatis.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FolderSevice {

    @Autowired
    private FolderMapper folderMapper;

    /**
     * 删除目录。并循环删除子目录
     * 注意：调用前需确保目录下没有文件，此方法不做检查！
     * @param folderId
     */
    public void delete(String folderId) {

        folderMapper.delete(folderId);

        Folder folderParam = new Folder();
        folderParam.setParentId(folderId);
        Page page = new Page<>(1, Integer.MAX_VALUE);
        List<Folder> folders = folderMapper.getByPage(page, folderParam);
        for (Folder f : folders) {
            this.delete(f.getId());
        }
    }

    public Map<String, String> getAllFolderNameMap() {
        Map<String, String> result = new HashMap<>();
        List<Folder> folders = folderMapper.getAllFolder();
        for (Folder f : folders) {
            result.put(f.getId(), f.getTitle());
        }

        return result;
    }
}
