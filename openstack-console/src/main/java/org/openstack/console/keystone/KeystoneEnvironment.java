package org.openstack.console.keystone;

import org.apache.commons.cli.CommandLine;
import org.openstack.console.Command;
import org.openstack.console.Console;
import org.openstack.console.Environment;
import org.openstack.keystone.KeystoneClient;
import org.openstack.keystone.api.Authenticate;
import org.openstack.keystone.model.Access;

public class KeystoneEnvironment extends Environment {
	
	public final KeystoneClient CLIENT;
	
	public static final Command KEYSTONE = new Command("keystone") {
		
		@Override
		public void execute(Console console, CommandLine args) {
			
			KeystoneClient client = new KeystoneClient(console.getProperty("keystone.endpoint"));
			
			Access access = client.execute(Authenticate.withPasswordCredentials(
					console.getProperty("keystone.username"), 
					console.getProperty("keystone.password")
			).withTenantName(console.getProperty("keystone.tenant_name")));
					
			client.token(access.getToken().getId());
			
			KeystoneEnvironment environment = new KeystoneEnvironment(console.getEnvironment(), client);
			
			environment.register(new KeystoneTenantList());
			
			console.setEnvironment(environment);
		}
		
	};
	
	public KeystoneEnvironment(Environment parent, KeystoneClient client) {
		super(parent);
		CLIENT = client;
	}

	/* (non-Javadoc)
	 * @see org.woorea.wsh.Environment#getPrompt()
	 */
	@Override
	public String getPrompt() {
		return "keystone> ";
	}
	
}
