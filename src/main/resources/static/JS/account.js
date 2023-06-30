const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      client: [],
      accounts: [],
      transactions: [],
      id: "",
      amountFormated: [],
      filterAccount: [],
      logged: false,
    };
  },

  created() {
    this.loadData();
  },
  methods: {
    loadData() {
      axios
        .get(`http://localhost:8080/api/clients/current`)
        .then((response) => {
          this.logged == true;
          this.client = response.data;
          console.log(this.client);
          this.accounts = this.client.accounts;
          console.log(this.accounts);
          this.id = new URLSearchParams(location.search).get("id"); //te devuelve los parametros de la url
          this.filterAccount = this.accounts.find((account) => {
            return this.id == account.id;
          });
          console.log(this.filterAccount);
          this.transactions = this.filterAccount.transactions;
          console.log(this.transactions);
          this.transactions.sort((a, b) => b.id - a.id);
          /* EDIT OBJECT TRANSACTIONS */
          this.transactions.forEach((transaction) => {
            transaction.time = transaction.date.slice(11, 19);
          });
          this.transactions.forEach((transaction) => {
            transaction.date = transaction.date.slice(0, 10);
          });
          /* "en-US" =  formatea el número según la configuración regional 
         style = Se utiliza para especificar el tipo de formato que desea. Esto toma valores como decimales, moneda y unidades.
         currency = Puede usar esta opción para especificar la moneda a la que desea formatear, como 'USD', 'CAD', 'GBP``', 'INR' y muchas más.
         */
          this.amountFormated = new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: "USD",
          });
          this.transactions.forEach((transaction) => {
            transaction.amount = this.amountFormated.format(transaction.amount);
          });
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
