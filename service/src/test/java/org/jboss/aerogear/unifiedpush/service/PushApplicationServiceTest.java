/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.unifiedpush.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.jboss.aerogear.unifiedpush.api.Alias;
import org.jboss.aerogear.unifiedpush.api.DocumentMetadata;
import org.jboss.aerogear.unifiedpush.api.PushApplication;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Test;

import com.datastax.driver.core.utils.UUIDs;

public class PushApplicationServiceTest extends AbstractBaseServiceTest {

	@Inject
	private DocumentService documentService;

	@Inject
	private AliasService aliasService;

    @Test
    @Transactional(TransactionMode.ROLLBACK)
    public void addPushApplication() {

        PushApplication pa = new PushApplication();
        pa.setName("EJB Container");
        final String uuid = UUID.randomUUID().toString();
        pa.setPushApplicationID(uuid);

        pushApplicationService.addPushApplication(pa);

        PushApplication stored = pushApplicationService.findByPushApplicationID(uuid);
        assertThat(stored).isNotNull();
        assertThat(stored.getId()).isNotNull();
        assertThat(pa.getName()).isEqualTo(stored.getName());
        assertThat(pa.getPushApplicationID()).isEqualTo(stored.getPushApplicationID());
    }

    @Test
    @Transactional(TransactionMode.ROLLBACK)
    public void updatePushApplication() {
        PushApplication pa = new PushApplication();
        pa.setName("EJB Container");
        final String uuid = UUID.randomUUID().toString();
        pa.setPushApplicationID(uuid);

        pushApplicationService.addPushApplication(pa);

        PushApplication stored = pushApplicationService.findByPushApplicationID(uuid);
        assertThat(stored).isNotNull();

        stored.setName("FOO");
        pushApplicationService.updatePushApplication(stored);
        stored = pushApplicationService.findByPushApplicationID(uuid);
        assertThat("FOO").isEqualTo(stored.getName());
    }

    @Test
    @Transactional(TransactionMode.ROLLBACK)
    public void findByPushApplicationID() {
        PushApplication pa = new PushApplication();
        pa.setName("EJB Container");
        final String uuid = UUID.randomUUID().toString();
        pa.setPushApplicationID(uuid);

        pushApplicationService.addPushApplication(pa);

        PushApplication stored = pushApplicationService.findByPushApplicationID(uuid);
        assertThat(stored).isNotNull();
        assertThat(stored.getId()).isNotNull();
        assertThat(pa.getName()).isEqualTo(stored.getName());
        assertThat(pa.getPushApplicationID()).isEqualTo(stored.getPushApplicationID());

        stored = pushApplicationService.findByPushApplicationID("123");
        assertThat(stored).isNull();

    }

    @Test
    @Transactional(TransactionMode.ROLLBACK)
    public void findAllPushApplicationsForDeveloper() {
        PushApplication pa = new PushApplication();
        pa.setName("EJB Container");
        final String uuid = UUID.randomUUID().toString();
        pa.setPushApplicationID(uuid);
        pa.setDeveloper("admin");

        pushApplicationService.addPushApplication(pa);

        assertThat(searchApplicationService.findAllPushApplicationsForDeveloper(0, 10).getResultList()).isNotEmpty();
        assertThat(searchApplicationService.findAllPushApplicationsForDeveloper(0, 10).getResultList()).hasSize(2);
    }

    @Test
    @Transactional(TransactionMode.ROLLBACK)
    public void removePushApplication() {
        PushApplication pa = new PushApplication();
        pa.setName("EJB Container");
        final String uuid = UUID.randomUUID().toString();
        pa.setPushApplicationID(uuid);
        pa.setDeveloper("admin");

        pushApplicationService.addPushApplication(pa);

        assertThat(searchApplicationService.findAllPushApplicationsForDeveloper(0, 10).getResultList()).isNotEmpty();
        assertThat(searchApplicationService.findAllPushApplicationsForDeveloper(0, 10).getResultList()).hasSize(2);

        pushApplicationService.removePushApplication(pa);

        assertThat(searchApplicationService.findAllPushApplicationsForDeveloper(0, 10).getResultList()).hasSize(1);
        assertThat(pushApplicationService.findByPushApplicationID(uuid)).isNull();
    }

    @Test
    @Transactional(TransactionMode.ROLLBACK)
    public void findByPushApplicationIDForDeveloper() {
        PushApplication pa = new PushApplication();
        pa.setName("EJB Container");
        final String uuid = UUID.randomUUID().toString();
        pa.setPushApplicationID(uuid);
        pa.setDeveloper("admin");

        pushApplicationService.addPushApplication(pa);

        PushApplication queried =  searchApplicationService.findByPushApplicationIDForDeveloper(uuid);
        assertThat(queried).isNotNull();
        assertThat(uuid).isEqualTo(queried.getPushApplicationID());

        assertThat(searchApplicationService.findByPushApplicationIDForDeveloper("123-3421")).isNull();
    }

    @Test
    @Transactional(TransactionMode.ROLLBACK)
    public void removeApplicationAndDocuments() {
        PushApplication pa = new PushApplication();
        pa.setName("EJB Container");
        final String uuid = UUID.randomUUID().toString();
        pa.setPushApplicationID(uuid);
        pa.setDeveloper("admin");

        pushApplicationService.addPushApplication(pa);

    	Alias alias = new Alias(UUID.fromString(pa.getPushApplicationID()), UUIDs.timeBased(), "TEST@X.com");
		aliasService.create(alias);

        documentService.save(new DocumentMetadata(pa.getPushApplicationID(), "TASKS", alias, "1", null), "{SIMPLE}");
        List<String> documents = documentService.getLatestFromAliases(pa, alias.getEmail(), "1");
        assertThat(documents.size() == 1);

        aliasService.create(new Alias(UUID.fromString(pa.getPushApplicationID()), UUIDs.timeBased(), "TEST@X.com"));
        assertThat(aliasService.find(pa.getPushApplicationID(), alias.getEmail()) != null);

        pushApplicationService.removePushApplication(pa);
        documents = documentService.getLatestFromAliases(pa, alias.getEmail(), "1");
        assertThat(documents.size() == 0);
        assertThat(aliasService.find(pa.getPushApplicationID(), alias.getEmail()) == null);


    }

	@Override
	protected void specificSetup() {
	}
}
