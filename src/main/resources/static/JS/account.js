const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      allAccounts: [],
      account: [],
      transactions: [],
      id: "",
      amountFormated: [],
    };
  },

  created() {
    const parameters = new URLSearchParams(location.search); //te devuelve los parametros de la url
    this.id = parameters.get("id");
    axios
      .get(`http://localhost:8080/api/accounts/${this.id}`)
      .then((response) => {
        console.log(response);
        this.account = response.data;
        this.transactions = this.account.transactions;
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
        console.log(this.transactions);
      });
  },
  methods: {},
});

app.mount("#app");
