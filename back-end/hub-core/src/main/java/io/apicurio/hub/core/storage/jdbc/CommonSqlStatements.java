/*
 * Copyright 2017 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apicurio.hub.core.storage.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared base class for all sql statements.
 * @author eric.wittmann@gmail.com
 */
public abstract class CommonSqlStatements implements ISqlStatements {

    /**
     * Returns the database type identifier.
     */
    protected abstract String dbType();

    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#databaseInitialization()
     */
    @Override
    public List<String> databaseInitialization() {
        DdlParser parser = new DdlParser();
        try (InputStream input = getClass().getResourceAsStream("hub_" + dbType() + ".ddl")) {
            return parser.parse(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#databaseUpgrade(int, int)
     */
    @Override
    public List<String> databaseUpgrade(int fromVersion, int toVersion) {
        List<String> statements = new ArrayList<>();
        DdlParser parser = new DdlParser();
        
        for (int version = fromVersion + 1; version <= toVersion; version++) {
            try (InputStream input = getClass().getResourceAsStream("upgrade-" + version + "_" + dbType() + ".ddl")) {
                statements.addAll(parser.parse(input));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        return statements;
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#getDatabaseVersion()
     */
    @Override
    public String getDatabaseVersion() {
        return "SELECT a.prop_value FROM apicurio a WHERE a.prop_name = ?";
    }

    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#insertLinkedAccount()
     */
    @Override
    public String insertLinkedAccount() {
        return "INSERT INTO accounts (user_id, type, linked_on, used_on, nonce) VALUES (?, ?, ?, ?, ?)";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectLinkedAccountByType()
     */
    @Override
    public String selectLinkedAccountByType() {
        return "SELECT a.* FROM accounts a WHERE a.user_id = ? AND a.type = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#updateLinkedAccount()
     */
    @Override
    public String updateLinkedAccount() {
        return "UPDATE accounts SET used_on = ?, linked_on = ?, nonce = ? WHERE user_id = ? AND type = ?";
    }

    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectLinkedAccounts()
     */
    @Override
    public String selectLinkedAccounts() {
        return "SELECT a.* FROM accounts a WHERE a.user_id = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#deleteLinkedAccount()
     */
    @Override
    public String deleteLinkedAccount() {
        return "DELETE FROM accounts WHERE user_id = ? AND type = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#deleteLinkedAccounts()
     */
    @Override
    public String deleteLinkedAccounts() {
        return "DELETE FROM accounts WHERE user_id = ?";
    }

    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#insertApiDesign()
     */
    @Override
    public String insertApiDesign() {
        return "INSERT INTO api_designs (name, description, created_by, created_on, tags) VALUES (?, ?, ?, ?, ?)";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#deleteApiDesign()
     */
    @Override
    public String deleteApiDesign() {
        return "DELETE FROM api_designs WHERE id = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectApiDesigns()
     */
    @Override
    public String selectApiDesigns() {
        return "SELECT d.* FROM api_designs d JOIN acl a ON a.design_id = d.id WHERE a.user_id = ?";
    }

    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectApiDesignById()
     */
    @Override
    public String selectApiDesignById() {
        return "SELECT d.* FROM api_designs d JOIN acl a ON a.design_id = d.id WHERE d.id = ? AND a.user_id = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#updateApiDesign()
     */
    @Override
    public String updateApiDesign() {
        return "UPDATE api_designs SET name = ?, description = ?, tags = ? WHERE id = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#insertContent()
     */
    @Override
    public String insertContent() {
        return "INSERT INTO api_content (design_id, type, data, created_by, created_on) VALUES (?, ?, ?, ?, ?)";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#insertAcl()
     */
    @Override
    public String insertAcl() {
        return "INSERT INTO acl (user_id, design_id, role) VALUES (?, ?, ?)";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#deleteAcl()
     */
    @Override
    public String deleteAcl() {
        return "DELETE FROM acl WHERE user_id = ? AND design_id = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#updateAcl()
     */
    @Override
    public String updateAcl() {
        return "UPDATE acl SET role = ? WHERE user_id = ? AND design_id = ?";
    }

    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#clearContent()
     */
    @Override
    public String clearContent() {
        return "DELETE FROM api_content WHERE design_id = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#clearAcl()
     */
    @Override
    public String clearAcl() {
        return "DELETE FROM acl WHERE design_id = ?";
    }

    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#clearInvitations()
     */
    @Override
    public String clearInvitations() {
        return "DELETE FROM acl_invites WHERE design_id = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#hasOwnerPermission()
     */
    @Override
    public String hasOwnerPermission() {
        return "SELECT COUNT(*) "
                + "FROM acl a "
                + "WHERE a.design_id = ? AND a.user_id = ? AND a.role = 'owner'";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#hasWritePermission()
     */
    @Override
    public String hasWritePermission() {
        return "SELECT COUNT(*) "
                + "FROM acl a "
                + "WHERE a.design_id = ? AND a.user_id = ? AND (a.role = 'owner' OR a.role = 'collaborator')";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectPermissions()
     */
    @Override
    public String selectPermissions() {
        return "SELECT a.* FROM acl a WHERE a.design_id = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectApiDesignContributors()
     */
    @Override
    public String selectApiDesignContributors() {
        // TODO order by the # of edits and LIMIT the results to 5
        return "SELECT DISTINCT COUNT(c.created_by) as edits, c.created_by "
                + "FROM api_content c "
                + "JOIN acl a ON a.design_id = c.design_id "
                + "WHERE c.design_id = ? AND a.user_id = ? "
                + "GROUP BY c.created_by";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectLatestContentDocument()
     */
    @Override
    public String selectLatestContentDocument() {
        return "SELECT c.* "
                + "FROM api_content c "
                + "JOIN acl a ON a.design_id = c.design_id "
                + "WHERE c.design_id = ? AND c.type = 0 AND a.user_id = ? "
                + "ORDER BY c.version DESC LIMIT 1";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectContentCommands()
     */
    @Override
    public String selectContentCommands() {
        return "SELECT c.* "
                + "FROM api_content c "
                + "JOIN acl a ON a.design_id = c.design_id "
                + "WHERE c.design_id = ? AND c.type = 1 AND a.user_id = ? AND c.version > ? "
                + "ORDER BY c.version ASC";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#insertEditingSessionUuid()
     */
    @Override
    public String insertEditingSessionUuid() {
        return "INSERT INTO session_uuids (uuid, design_id, user_id, secret, version, expires_on) VALUES (?, ?, ?, ?, ?, ?)";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectEditingSessionUuid()
     */
    @Override
    public String selectEditingSessionUuid() {
        return "SELECT u.version "
                + "FROM session_uuids u "
                + "WHERE u.uuid = ? AND u.design_id = ? AND u.secret = ? AND u.expires_on > ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#updateCollaborationInvitationStatus()
     */
    @Override
    public String updateCollaborationInvitationStatus() {
        return "UPDATE acl_invites SET status = ?, modified_by = ?, modified_on = ? WHERE invite_id = ? AND status = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#insertCollaborationInvitation()
     */
    @Override
    public String insertCollaborationInvitation() {
        return "INSERT INTO acl_invites (created_by, created_on, created_by_display, design_id, role, invite_id, status) "
                  + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectCollaborationInvitations()
     */
    @Override
    public String selectCollaborationInvitations() {
        return "SELECT i.* "
                + "FROM acl_invites i "
                + "JOIN acl a ON a.design_id = i.design_id "
                + "WHERE i.design_id = ? AND a.user_id = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#selectCollaborationInvitation()
     */
    @Override
    public String selectCollaborationInvitation() {
        return "SELECT i.* "
                + "FROM acl_invites i "
                + "WHERE i.design_id = ? AND i.invite_id = ?";
    }
    
    /**
     * @see io.apicurio.hub.core.storage.jdbc.ISqlStatements#deleteEditingSessionUuid()
     */
    @Override
    public String deleteEditingSessionUuid() {
        return "DELETE FROM session_uuids WHERE uuid = ? AND design_id = ? AND secret = ? AND expires_on > ?";
    }
    
}
