package com.ofs.server.filter;

import com.ofs.server.filter.views.Public;
import com.ofs.server.model.OFSEntity;
import com.ofs.server.security.Subject;

public class DefaultFilter implements Filter<OFSEntity> {
    @Override
    public Class<? extends Public> filterView(OFSEntity ofsEntity, Subject subject) {
        return Public.class;
    }
}
