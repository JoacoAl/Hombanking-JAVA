const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      cards: [],
      client: [],
      credit: [],
      debit: [],
      logged: false,
    };
  },

  created() {
    axios.get("http://localhost:8080/api/clients/current").then((response) => {
      this.client = response.data;
      this.cards = response.data.cards;
      this.cards.forEach((card) => {
        if (card.type == "CREDIT") {
          this.credit.push(card);
        } else if (card.type == "DEBIT") {
          this.debit.push(card);
        }
      });
      console.log(this.credit);
      console.log(this.debit);

      this.logged = true;
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
