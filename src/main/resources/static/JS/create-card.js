const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      client: [],
      cardColor: null,
      cardType: null,
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
          this.logged = true;
        });
    },
    logOut() {
      axios.post("/api/logout").then((response) => {
        console.log("signed out!!!");
        this.logged = false;
      });
    },
    createCard() {
      axios
        .post(
          "/api/clients/current/cards",
          `cardType=${this.cardType}&cardColor=${this.cardColor}`,
          { headers: { "content-type": "application/x-www-form-urlencoded" } }
        )
        .then((response) => {
          window.location.href = "http://localhost:8080/web/cards.html";
        })
        .catch((err) => {
          this.errorModal();
        });
    },
    errorModal() {
      document.getElementById("errorModalCreateCards").style.display = "block";
    },
    closeModal() {
      document.getElementById("errorModalCreateCards").style.display = "none";
    },
  },
});

app.mount("#app");
