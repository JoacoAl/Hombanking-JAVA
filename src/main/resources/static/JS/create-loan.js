const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      objectLoan: {
        name: "",
        maxAmount: null,
        payments: [],
        percentage: null,
      },
      paymentsInput: null,
      amountFormat: [],
      amountFormated: "",
    };
  },

  created() { },

  methods: {
    createLoan() {
      this.objectLoan.payments = this.paymentsInput
        .split(",")
        .map((payment) => parseInt(payment.trim()));
      console.log(this.objectLoan.payments);
      console.log(this.objectLoan.name);
      console.log(this.objectLoan.maxAmount);
      axios.post("/api/createLoans", this.objectLoan).then((response) => {
        console.log("loan creado");
        this.confirmationTransferModal()((this.paymentsInput = "")),
          (this.objectLoan.maxAmount = null);
        this.objectLoan.name = "";
        this.objectLoan.percentage = null;
      });
    },
    confirmationTransferModal() {
      document.getElementById("confirmationTransferModal").style.display =
        "block";
    },
    closeModal() {
      document.getElementById("confirmationModal").style.display = "none";
    },
  },

  computed: {
    formatUSD() {
      this.amountFormat = new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
      });
      this.amountFormated = this.amountFormat.format(this.objectLoan.maxAmount);
    },
  },
});

app.mount("#app");
