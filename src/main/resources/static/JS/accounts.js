const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      clients: [],
      accounts: [],
      loans: [],
      id: [],
      amountFormated: [],
    };
  },

  created() {
    const parameters = new URLSearchParams(location.search); //te devuelve los parametros de la url
    this.id = parameters.get("id");
    axios.get("http://localhost:8080/api/clients/1").then((response) => {
      this.clients = response.data;
      this.accounts = this.clients.accounts;
      this.loans = this.clients.loans;
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
});

app.mount("#app");
