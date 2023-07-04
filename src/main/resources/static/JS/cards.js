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
    this.loadData();
  },
  methods: {
    loadData() {
      axios
        .get("http://localhost:8080/api/clients/current")
        .then((response) => {
          this.client = response.data;
          this.cards = response.data.cards;
          this.cards.forEach((card) => {
            if (card.type == "CREDIT") {
              this.credit.push(card);
            } else if (card.type == "DEBIT") {
              this.debit.push(card);
            }
          });
          if (this.debit.length == 3 && this.credit.length == 3) {
            document.getElementById("btnCreateCardDebit").style.display =
              "none";
          } else {
            document.getElementById("btnCreateCardDebit").style.display =
              "block";
          }
          this.logged = true;
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
