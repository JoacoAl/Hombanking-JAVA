const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      client: [],
      accounts: [],
      loans: [],
      logged: false,
      amountFormat: [],
      amountFormated: "",
      /*v-models*/
      loanSelected: "prueba",
      amount: 0,
      dues: 0,
      destinationAccountNumber: "",
      transferObject: {},
    };
  },

  created() {
    this.loadData();
  },

  methods: {
    loadData() {
      axios
        .get("/api/clients/current")
        .then((response) => {
          console.log(response);
          this.client = response.data;
          this.accounts = this.client.accounts;
          this.logged = true;
          console.log(this.accounts);
        })
        .catch((err) => console.error());
      axios.get("/api/loans").then((response) => {
        this.loans = response.data;
        this.loans.forEach((loan) => {
          loan.maxAmount = this.amountFormat.format(loan.maxAmount);
        });
        console.log(this.loans);
      });
    },
    logOut() {
      axios.post("/api/logout").then((response) => {
        console.log("signed out!!!");
        this.logged = false;
      });
    },
    requestLoan() {
      this.transferObject = {
        id: this.loanSelected.id,
        amount: this.amount,
        dues: this.dues,
        destinationAccountNumber: this.destinationAccountNumber,
      };
      console.log(this.transferObject);
      axios
        .post("/api/loans", this.transferObject)
        .then((response) => {
          console.log("Listo");
          this.closeModal();
          this.confirmationRequestModal();
        })
        .catch((err) => {
          this.closeModal();
          this.modalErrorRequestLoan();
        });
    },
    missingData() {
      if (
        this.loanSelected.id == 0 ||
        this.amount == 0.0 ||
        this.dues == 0 ||
        this.destinationAccountNumber == ""
      ) {
        this.missingDataModal();
      } else {
        this.confirmationModal();
      }
    },
    /*     modal 1*/
    closeModal() {
      document.getElementById("confirmationModal").style.display = "none";
    },
    confirmationModal() {
      document.getElementById("confirmationModal").style.display = "block";
    },

    /* modal 2 */
    confirmationRequestModal() {
      document.getElementById("confirmationRequestModal").style.display =
        "block";
    },
    /* modal 3 */
    missingDataModal() {
      document.getElementById("missingDataModal").style.display = "block";
    },
    closeMissingDataModal() {
      document.getElementById("missingDataModal").style.display = "none";
    },
    /* modal 4 */
    modalErrorRequestLoan() {
      document.getElementById("modalErrorRequestLoan").style.display = "block";
    },
    closeModalErrorRequestLoan() {
      document.getElementById("modalErrorRequestLoan").style.display = "none";
    },
  },
  computed: {
    formatUSD() {
      this.amountFormat = new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
      });
      this.amountFormated = this.amountFormat.format(this.amount);
    },
  },
});

app.mount("#app");
