const request = require("supertest");

const { createServer, resetAccounts } = require("../../server");

describe("Tests d integration - API bancaire JavaScript", () => {
  beforeEach(() => {
    resetAccounts();
  });

  it("cree deux comptes et liste les comptes", async () => {
    const app = createServer();

    await request(app).post("/accounts").send({
      full_name: "AMAYEN BOUSSIOM THIERRY MARTIN",
      phone_number: "699001122",
      initial_balance: 10000,
    });

    await request(app).post("/accounts").send({
      full_name: "Compte Destinataire",
      phone_number: "677001122",
      initial_balance: 5000,
    });

    const response = await request(app).get("/accounts");

    expect(response.statusCode).toBe(200);
    expect(response.body).toHaveLength(2);
  });

  it("effectue un depot", async () => {
    const app = createServer();
    const created = await request(app).post("/accounts").send({
      full_name: "Thierry Martin",
      phone_number: "699001122",
      initial_balance: 1000,
    });

    const response = await request(app)
      .post(`/accounts/${created.body.id}/deposit`)
      .send({ amount: 500, description: "Depot test" });

    expect(response.statusCode).toBe(200);
    expect(response.body.balance).toBe(1500);
  });

  it("effectue un retrait valide", async () => {
    const app = createServer();
    const created = await request(app).post("/accounts").send({
      full_name: "Thierry Martin",
      phone_number: "699001122",
      initial_balance: 3000,
    });

    const response = await request(app)
      .post(`/accounts/${created.body.id}/withdraw`)
      .send({ amount: 1000, description: "Retrait test" });

    expect(response.statusCode).toBe(200);
    expect(response.body.balance).toBe(2000);
  });

  it("refuse un retrait avec solde insuffisant", async () => {
    const app = createServer();
    const created = await request(app).post("/accounts").send({
      full_name: "Thierry Martin",
      phone_number: "699001122",
      initial_balance: 200,
    });

    const response = await request(app)
      .post(`/accounts/${created.body.id}/withdraw`)
      .send({ amount: 500 });

    expect(response.statusCode).toBe(400);
    expect(response.body.detail).toMatch(/Solde insuffisant/);
  });

  it("effectue un virement entre deux comptes", async () => {
    const app = createServer();
    const source = await request(app).post("/accounts").send({
      full_name: "Source",
      phone_number: "699001122",
      initial_balance: 4000,
    });
    const destination = await request(app).post("/accounts").send({
      full_name: "Destination",
      phone_number: "677001122",
      initial_balance: 1000,
    });

    const response = await request(app)
      .post(`/accounts/${source.body.id}/transfer`)
      .send({
        to_account_id: destination.body.id,
        amount: 1500,
        description: "Virement test",
      });

    expect(response.statusCode).toBe(200);
    expect(response.body.from_account.balance).toBe(2500);
    expect(response.body.to_account.balance).toBe(2500);
    expect(response.body.from_account.transactions.at(-1).transaction_type).toBe("transfer_out");
    expect(response.body.to_account.transactions.at(-1).transaction_type).toBe("transfer_in");
  });

  it("refuse un virement vers le meme compte", async () => {
    const app = createServer();
    const account = await request(app).post("/accounts").send({
      full_name: "Source",
      phone_number: "699001122",
      initial_balance: 4000,
    });

    const response = await request(app)
      .post(`/accounts/${account.body.id}/transfer`)
      .send({
        to_account_id: account.body.id,
        amount: 500,
      });

    expect(response.statusCode).toBe(400);
  });

  it("retourne les transactions d un compte", async () => {
    const app = createServer();
    const account = await request(app).post("/accounts").send({
      full_name: "Thierry Martin",
      phone_number: "699001122",
      initial_balance: 1000,
    });

    await request(app).post(`/accounts/${account.body.id}/deposit`).send({ amount: 200 });
    await request(app).post(`/accounts/${account.body.id}/withdraw`).send({ amount: 100 });

    const response = await request(app).get(`/accounts/${account.body.id}/transactions`);

    expect(response.statusCode).toBe(200);
    expect(response.body).toHaveLength(3);
  });
});
