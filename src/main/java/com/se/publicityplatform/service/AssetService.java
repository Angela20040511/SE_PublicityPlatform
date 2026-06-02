package com.se.publicityplatform.service;

import com.se.publicityplatform.mapper.PublicityAssetMapper;
import com.se.publicityplatform.vo.ArchivedAssetView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {

    private final PublicityAssetMapper assetMapper;

    public AssetService(PublicityAssetMapper assetMapper) {
        this.assetMapper = assetMapper;
    }

    public List<ArchivedAssetView> archivedAssets() {
        return assetMapper.findAllViews();
    }
}
