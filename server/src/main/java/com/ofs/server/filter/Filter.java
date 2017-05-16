package com.ofs.server.filter;

import com.ofs.server.filter.views.Public;
import com.ofs.server.model.OFSEntity;
import com.ofs.server.security.Subject;

public interface Filter<T extends OFSEntity> {
    Class<? extends Public> filterView(T entity, Subject subject);
}
