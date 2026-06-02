package com.se.publicityplatform.mapper;

import com.se.publicityplatform.model.PublicityAsset;
import com.se.publicityplatform.vo.ArchivedAssetView;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PublicityAssetMapper {

    @Insert("""
            insert into publicity_asset (request_id, task_id, asset_type, file_url, archived_by, description)
            values (#{requestId}, #{taskId}, #{assetType}, #{fileUrl}, #{archivedBy}, #{description})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "assetId")
    int insert(PublicityAsset asset);

    @Select("select count(*) from publicity_asset")
    int countAll();

    @Select("""
            select * from publicity_asset
            order by archived_at desc
            """)
    List<PublicityAsset> findAll();

    @Select("""
            select
                r.activity_name,
                a.task_id,
                a.asset_type,
                a.file_url,
                a.archived_at,
                a.description
            from publicity_asset a
            join publicity_request r on r.request_id = a.request_id
            order by a.archived_at desc
            """)
    List<ArchivedAssetView> findAllViews();
}
