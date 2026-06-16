const {
  round,
  generateAccountNumber,
  accountSummary,
  accountDetails,
  findAccount,
  resetAccounts,
  accounts,
} = require("../../server");

describe("Tests unitaires - fonctions utilitaires", () => {
  beforeEach(() => {
    resetAccounts();
  });

  it("arrondit un montant a deux decimales", () => {
    expect(round(1250.456)).toBe(1250.46);
    expect(round("12.994")).toBe(12.99);
  });

  it("genere un numero de compte au bon format", () => {
    expect(generateAccountNumber()).toMatch(/^ACC-\d{8}-[A-Z0-9]{8}$/);
  });

  it("retourne un resume de compte sans transactions", () => {
    const account = {
      id: "acc-1",
      account_number: "ACC-20260616-ABCDEFGH",
      full_name: "Thierry Martin",
      phone_number: "699001122",
      email: "thierry@example.com",
      balance: 5000,
      created_at: "2026-06-16T00:00:00.000Z",
      transactions: [{ transaction_type: "deposit" }],
    };

    expect(accountSummary(account)).toEqual({
      id: "acc-1",
      account_number: "ACC-20260616-ABCDEFGH",
      full_name: "Thierry Martin",
      phone_number: "699001122",
      email: "thierry@example.com",
      balance: 5000,
      created_at: "2026-06-16T00:00:00.000Z",
    });
  });

  it("retourne le detail avec transactions", () => {
    const account = {
      id: "acc-1",
      account_number: "ACC-20260616-ABCDEFGH",
      full_name: "Thierry Martin",
      phone_number: "699001122",
      email: null,
      balance: 5000,
      created_at: "2026-06-16T00:00:00.000Z",
      transactions: [{ transaction_type: "deposit", amount: 5000 }],
    };

    expect(accountDetails(account).transactions).toHaveLength(1);
  });

  it("retrouve un compte par identifiant", () => {
    accounts.push({ id: "acc-1" }, { id: "acc-2", full_name: "Compte 2" });
    expect(findAccount("acc-2").full_name).toBe("Compte 2");
    expect(findAccount("absent")).toBeUndefined();
  });
});
