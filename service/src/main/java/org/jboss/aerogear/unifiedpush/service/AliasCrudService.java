package org.jboss.aerogear.unifiedpush.service;

import java.util.UUID;

import org.jboss.aerogear.unifiedpush.api.Alias;

/**
 * Public Service which wraps Spring DAO. <br/>
 * TODO - As soon as can mixup Spting and EJB IOC, merge this service into
 * AliasService.
 */
public interface AliasCrudService {
	Alias find(String alias);

	void remove(String alias);

	void removeAll(UUID pushApplicationId);

	void create(Alias alias);
}