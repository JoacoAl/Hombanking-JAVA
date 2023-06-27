const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      client: [],
      accounts: [],
      loans: [],
      amountFormated: [],
      logged: false,
    };
  },

  created() {
    axios.get("http://localhost:8080/api/clients/current").then((response) => {
      this.client = response.data;
      console.log(this.clients);
      this.logged = true;
      this.accounts = this.client.accounts;
      this.loans = this.client.loans;
      this.amountFormated = new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
      });
      this.accounts.forEach((account) => {
        account.balance = this.amountFormated.format(account.balance);
      });
      this.loans.forEach((loan) => {
        loan.amount = this.amountFormated.format(loan.amount);
      });
      console.log(this.loans);
    });
  },
  methods: {
    logOut() {
      axios.post("/api/logout").then((response) => {
        console.log("signed out!!!");
        this.logged = false;
      });
    },
  },
});

app.mount("#app");
