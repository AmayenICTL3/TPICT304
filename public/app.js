const state = {
  accounts: [],
  selectedAccountId: "",
  selectedAccount: null,
};

const accountListEl = document.getElementById("account-list");
const detailEl = document.getElementById("account-detail");
const transactionsEl = document.getElementById("transactions");
const statusEl = document.getElementById("status");

function setStatus(message, tone = "") {
  statusEl.className = `status ${tone}`.trim();
  statusEl.textContent = message || "";
}

async function api(path, options = {}) {
  const response = await fetch(path, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options,
  });
  const text = await response.text();
  const data = text ? JSON.parse(text) : null;
  if (!response.ok) {
    throw new Error(data?.detail || `Erreur HTTP ${response.status}`);
  }
  return data;
}

function money(value) {
  return `${Number(value || 0).toLocaleString("fr-FR", { minimumFractionDigits: 2, maximumFractionDigits: 2 })} XAF`;
}

function renderStats() {
  const balances = state.accounts.reduce((sum, account) => sum + Number(account.balance || 0), 0);
  document.getElementById("stat-count").textContent = String(state.accounts.length);
  document.getElementById("stat-total").textContent = money(balances);
  document.getElementById("stat-selected").textContent = state.selectedAccount ? state.selectedAccount.account_number : "Aucun";
  document.getElementById("stat-transactions").textContent = state.selectedAccount?.transactions?.length || 0;
}

function renderAccounts() {
  if (!state.accounts.length) {
    accountListEl.innerHTML = '<div class="empty">Aucun compte disponible.</div>';
    return;
  }

  accountListEl.innerHTML = state.accounts.map((account) => `
    <article class="account-row">
      <div>
        <h4>${account.full_name}</h4>
        <p>${account.account_number}</p>
        <p>${account.phone_number}${account.email ? ` · ${account.email}` : ""}</p>
      </div>
      <div class="account-actions">
        <strong>${money(account.balance)}</strong>
        <button class="button" data-select="${account.id}">Ouvrir</button>
      </div>
    </article>
  `).join("");

  accountListEl.querySelectorAll("[data-select]").forEach((button) => {
    button.addEventListener("click", () => {
      document.getElementById("selected-account-id").value = button.dataset.select;
      loadAccount(button.dataset.select);
    });
  });
}

function renderDetail() {
  if (!state.selectedAccount) {
    detailEl.innerHTML = '<div class="empty">Selectionne un compte.</div>';
    transactionsEl.innerHTML = '<div class="empty">Aucune transaction a afficher.</div>';
    renderStats();
    return;
  }

  const account = state.selectedAccount;
  detailEl.innerHTML = `
    <div class="detail-block">
      <h4>${account.full_name}</h4>
      <p>${account.account_number}</p>
      <p>${account.phone_number}${account.email ? ` · ${account.email}` : ""}</p>
      <p>Solde : <strong>${money(account.balance)}</strong></p>
      <p>Cree le ${new Date(account.created_at).toLocaleString("fr-FR")}</p>
    </div>
  `;

  if (!account.transactions?.length) {
    transactionsEl.innerHTML = '<div class="empty">Aucune transaction enregistree.</div>';
    renderStats();
    return;
  }

  transactionsEl.innerHTML = account.transactions.map((transaction) => {
    const tone = transaction.transaction_type === "transfer_out"
      ? "out"
      : transaction.transaction_type === "transfer_in"
        ? "in"
        : "other";

    return `
      <div class="txn-block ${tone}">
        <h4>${transaction.transaction_type}</h4>
        <p>Montant : ${money(transaction.amount)}</p>
        <p>Solde apres : ${money(transaction.balance_after)}</p>
        <p>${transaction.description || "Sans description"}</p>
        <p>${new Date(transaction.created_at).toLocaleString("fr-FR")}</p>
      </div>
    `;
  }).join("");

  renderStats();
}

async function refreshAccounts(preserveSelection = true) {
  state.accounts = await api("/accounts");
  renderAccounts();
  if (preserveSelection && state.selectedAccountId && state.accounts.some((account) => account.id === state.selectedAccountId)) {
    await loadAccount(state.selectedAccountId, false);
    return;
  }
  state.selectedAccountId = "";
  state.selectedAccount = null;
  renderDetail();
}

async function loadAccount(accountId, announce = true) {
  if (!accountId) {
    throw new Error("Identifiant de compte manquant.");
  }
  state.selectedAccountId = accountId;
  state.selectedAccount = await api(`/accounts/${accountId}`);
  renderDetail();
  if (announce) {
    setStatus("Compte charge.", "success");
  }
}

document.getElementById("create-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  try {
    const created = await api("/accounts", {
      method: "POST",
      body: JSON.stringify({
        full_name: form.get("full_name"),
        phone_number: form.get("phone_number"),
        email: form.get("email") || undefined,
        initial_balance: form.get("initial_balance") || 0,
      }),
    });
    event.currentTarget.reset();
    await refreshAccounts(false);
    document.getElementById("selected-account-id").value = created.id;
    await loadAccount(created.id, false);
    setStatus("Compte cree.", "success");
  } catch (error) {
    setStatus(error.message, "error");
  }
});

document.getElementById("selected-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    await loadAccount(document.getElementById("selected-account-id").value);
  } catch (error) {
    setStatus(error.message, "error");
  }
});

async function submitOperation(formId, pathSuffix, successMessage) {
  const form = document.getElementById(formId);
  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    try {
      await api(`/accounts/${data.get("account_id")}/${pathSuffix}`, {
        method: "POST",
        body: JSON.stringify({
          amount: data.get("amount"),
          description: data.get("description"),
        }),
      });
      await refreshAccounts();
      setStatus(successMessage, "success");
    } catch (error) {
      setStatus(error.message, "error");
    }
  });
}

submitOperation("deposit-form", "deposit", "Depot enregistre.");
submitOperation("withdraw-form", "withdraw", "Retrait enregistre.");

document.getElementById("transfer-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const data = new FormData(event.currentTarget);
  try {
    const response = await api(`/accounts/${data.get("from_account_id")}/transfer`, {
      method: "POST",
      body: JSON.stringify({
        to_account_id: data.get("to_account_id"),
        amount: data.get("amount"),
        description: data.get("description"),
      }),
    });
    await refreshAccounts(false);
    document.getElementById("selected-account-id").value = response.from_account.id;
    await loadAccount(response.from_account.id, false);
    setStatus("Virement enregistre.", "success");
  } catch (error) {
    setStatus(error.message, "error");
  }
});

document.getElementById("refresh-button").addEventListener("click", async () => {
  try {
    await refreshAccounts();
    setStatus("Liste actualisee.", "success");
  } catch (error) {
    setStatus(error.message, "error");
  }
});

refreshAccounts(false).catch((error) => setStatus(error.message, "error"));
