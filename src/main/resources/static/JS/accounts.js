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
    this.loadData();
  },
  methods: {
    loadData() {
      axios
        .get("http://localhost:8080/api/clients/current")
        .then((response) => {
          this.client = response.data;
          console.log(this.client);
          this.logged = true;
          this.accounts = this.client.accounts;
          console.log(this.accounts);
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
          if (this.accounts.length == 3) {
            document.getElementById("btnCreateAccount").style.display = "none";
          } else {
            document.getElementById("btnCreateAccount").style.display = "block";
          }
        });
    },
    logOut() {
      axios.post("/api/logout").then((response) => {
        console.log("signed out!!!");
        this.logged = false;
      });
    },
    createAccount() {
      axios
        .post("/api/clients/current/accounts")
        .then((response) => {
          this.loadData();
        })
        .catch((err) => console.log(err));
    },
  },
});

app.mount("#app");
