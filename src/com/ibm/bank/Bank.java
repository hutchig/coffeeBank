package com.ibm.bank;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("accounts")
@ApplicationScoped
public class Bank implements Accounts {

	private static Map<Customer, Integer> vault = getVault();
	private static Bank instance;

	public Bank() {
		instance = this;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String accounts() {
		String result = "";

		for (Customer person : vault.keySet()) {
			result += person + " $" + vault.get(person) + " ";
		}
		return result + "\n";

	}

	@GET
	@Path("/deposit")
	@Produces(MediaType.TEXT_PLAIN)
	public String deposit(@QueryParam("customer") String name, @QueryParam("amount") String amount) {
		Customer customer = Customer.getCustomer(name);
		try {
			increment(customer, Integer.parseInt(amount));
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		return Integer.toString(getBalance(customer));
	}

	@GET
	@Path("/withdraw")
	@Produces(MediaType.TEXT_PLAIN)
	public String withdraw(@QueryParam("customer") String name, @QueryParam("amount") String amount) {
		Customer customer = Customer.getCustomer(name);
		try {
			decrement(customer, Integer.parseInt(amount));
		} catch (NotEnoughFundsException e) {
			return "REFUSED";
		}
		return Integer.toString(getBalance(customer));
	}

	@Override
	public Integer decrement(Customer who, int debit) throws NotEnoughFundsException {

		int balance = getBalance(who);
		if (balance < debit) {
			throw new NotEnoughFundsException();
		}
		vault.put(who, balance - debit);
		Integer newBalance = getBalance(who);
		System.out.println(who + " has $" + newBalance);
		return newBalance;
	}

	@Override
	public Integer increment(Customer who, int credit) throws IOException {
		Integer balance = vault.get(who);
		vault.put(who, balance + credit);
		Integer newBalance = getBalance(who);
		System.out.println(who + " has $" + newBalance);
		return newBalance;
	}

	@Override
	public Integer getBalance(Customer who) {
		vault.putIfAbsent(who, 0);
		return vault.get(who);
	}

	private static Map<Customer, Integer> getVault() {
		Map<Customer, Integer> m = new HashMap<>();

		m.put(Customer.getCustomer("Bob"), 10);
		m.put(Customer.getCustomer("Sheila"), 10);
		m.put(Customer.getCustomer("Fred"), 10);
		m.put(Customer.getCustomer("Gordon"), 10);

		return m;

	}

	static Bank getBank() {
		return instance != null ? instance : new Bank();
	}

	public String toString() {
		return accounts();
	}

}
