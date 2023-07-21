const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      client: [],
      accounts: [],
      transactions: [],
      id: null,
      amountFormated: [],
      filterAccount: [],
      logged: false,
      activeTransactions: [],
    };
  },

  created() {
    this.loadData();
  },
  methods: {
    loadData() {
      axios.get(`/api/clients/current`).then((response) => {
        this.logged == true;

        this.client = response.data;

        this.accounts = this.client.accounts;
        this.id = new URLSearchParams(location.search).get("id");
        this.filterAccount = this.accounts.find((account) => {
          return this.id == account.id;
        });

        this.transactions = this.filterAccount.transactions;
        this.transactions.sort((a, b) => b.id - a.id);

        /* EDIT OBJECT TRANSACTIONS */
        this.amountFormated = new Intl.NumberFormat("en-US", {
          style: "currency",
          currency: "USD",
        });
        this.transactions.forEach((transaction) => {
          transaction.amount = this.amountFormated.format(transaction.amount);
          transaction.balance = this.amountFormated.format(transaction.balance);
        });

        this.activeTransactions = this.transactions.filter(
          (transaction) => transaction.active
        );

        console.log(this.activeTransactions);
      });
    },
    logOut() {
      axios.post("/api/logout").then((response) => {
        console.log("signed out!!!");
        this.logged = false;
      });
    },
  },
});

app.mount("#app");
