const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      cards: [],
      client: [],
      credit: [],
      debit: [],
      logged: false,
      cardNumber: "",
      currentDate: "",
      expired: null,
    };
  },

  created() {
    this.loadData();
    this.getCurrentDate();
  },
  methods: {
    loadData() {
      axios.get("/api/clients/current").then((response) => {
        this.client = response.data;
        this.logged = true;
      });
      this.getCards();
    },
    getCards() {
      axios.get("/api/getCards").then((response) => {
        console.log(response);
        this.cards = response.data;
        console.log(this.cards);
        this.cards.forEach((card) => {
          if (card.type == "CREDIT") {
            this.credit.push(card);
          } else if (card.type == "DEBIT") {
            this.debit.push(card);
          }
        });
        if (this.debit.length == 3 && this.credit.length == 3) {
          document.getElementById("btnCreateCardDebit").style.display = "none";
        } else {
          document.getElementById("btnCreateCardDebit").style.display = "block";
        }
      });
    },
    deleteCard(cardNumber) {
      console.log(cardNumber);
      /*       this.debit = this.debit.filter((card) => card.number === cardNumber);
            this.credit = this.debit.filter((card) => card.number === cardNumber); */
      axios
        .put("/api/clients/current/cards", `cardNumber=${cardNumber}`)
        .then((response) => {
          console.log("hola");
          console.log(this.cards);
          this.deletCardsOk();
        })
        .catch((err) => console.log(err.response.data));
    },
    getCurrentDate() {
      const getCurrentDate = new Date();
      const year = getCurrentDate.getFullYear();
      const month = getCurrentDate.getMonth() + 1;
      const day = getCurrentDate.getDate();

      this.currentDate = new Date(year, month - 1, day);

      console.log(this.currentDate);

      return this.currentDate;
    },
    checkExpiration(cardThruDate) {
      const limitDate = new Date(cardThruDate);
      limitDate.setMonth(limitDate.getMonth() - 1);

      return this.currentDate > new Date(cardThruDate);
    },

    warningExpirationCard(cardThruDate) {
      const limitDate = new Date(cardThruDate);
      limitDate.setMonth(limitDate.getMonth() - 1);

      const dateWarning = new Date(
        limitDate.getFullYear(),
        limitDate.getMonth(),
        limitDate.getDate()
      );

      console.log(dateWarning, "datewarning");
      console.log(this.currentDate, "currentdate");

      return (
        this.currentDate > dateWarning &&
        this.currentDate < new Date(cardThruDate)
      );
    },

    /* cosas */

    logOut() {
      axios.post("/api/logout").then((response) => {
        console.log("signed out!!!");
        this.logged = false;
      });
    },
    deletCardsOk() {
      document.getElementById("deletCardsOk").style.display = "block";
    },
    confirmationModal(cardNumber) {
      const modalNumber = `confirmationModal-${cardNumber}`;
      const modal = document.getElementById(modalNumber);
      console.log(modalNumber);
      modal.style.display = 'block';
    },
    closeModal() {
      document.getElementById("confirmationModal").style.display = "none";
    },
    closeModal(cardNumber) {
      const modalNumber = `confirmationModal-${cardNumber}`;
      const modal = document.getElementById(modalNumber);
      modal.style.display = 'none';
    },
  },
});

app.mount("#app");
