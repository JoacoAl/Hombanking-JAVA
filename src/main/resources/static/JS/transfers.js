const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      client: [],
      accounts: [],
      logged: false,
      amountFormat: [],
      amountFormated: "",
      transferObject: {
        amount: 0.0,
        description: "",
        numberAccount: "",
        destinationAccount: "",
      },
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
          console.log(response);
          this.client = response.data;
          this.accounts = this.client.accounts;
          this.logged = true;
          console.log(this.accounts);
          this.transferObject.numberAccount = new URLSearchParams(
            location.search
          ).get("number");
          console.log(this.transferObject.numberAccount);
        });
    },
    logOut() {
      axios.post("/api/logout").then((response) => {
        console.log("signed out!!!");
        this.logged = false;
      });
    },
    makeTransfer() {
      axios
        .post("/api/transactions", this.transferObject)
        .then((response) => {
          console.log("Listo");
          this.closeModal();
          this.confirmationTransferModal();
        })
        .catch((err) => {
          this.closeModal();
          this.modalErrorTransfer();
        });
    },
    missingData() {
      if (
        this.transferObject.amount == 0 ||
        this.transferObject.description == "" ||
        this.transferObject.numberAccount == "" ||
        this.transferObject.destinationAccount == ""
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
    confirmationTransferModal() {
      document.getElementById("confirmationTransferModal").style.display =
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
    modalErrorTransfer() {
      document.getElementById("modalErrorTransfer").style.display = "block";
    },
    closeModalErrorTransfer() {
      document.getElementById("modalErrorTransfer").style.display = "none";
    },
  },
  computed: {
    formatUSD() {
      this.amountFormat = new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
      });
      this.amountFormated = this.amountFormat.format(
        this.transferObject.amount
      );
    },
  },
});

app.mount("#app");
