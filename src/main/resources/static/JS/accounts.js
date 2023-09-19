const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      client: [],
      accounts: [],
      loans: [],
      amountFormated: [],
      logged: false,
      typeAccount: null,
      transferObject: {
        id: 3,
        startDate: "2023-07-19",
        endDate: "2023-07-24"
      }
    };
  },

  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      axios
        .get("/api/clients/current")
        .then((response) => {
          this.client = response.data;
          console.log(this.client);
          this.loans = this.client.loans;
          console.log(this.loans);
          this.amountFormated = new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: "USD",
          });
          this.loans.forEach((loan) => {
            loan.amount = this.amountFormated.format(loan.amount);
            loan.remainingAmount = this.amountFormated.format(
              loan.remainingAmount
            );
          });
          this.logged = true;
        })
        .catch((err) => console.log(err));
      axios
        .get("/api/getAccounts")
        .then((response) => {
          this.accounts = response.data;
          console.log(this.accounts);
          this.amountFormated = new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: "USD",
          });
          this.accounts.forEach((account) => {
            account.balance = this.amountFormated.format(account.balance);
          });
          if (this.accounts.length == 3) {
            document.getElementById("btnCreateAccount").style.display = "none";
          } else {
            document.getElementById("btnCreateAccount").style.display = "block";
          }
        })
        .catch((err) => console.log(err));
    },
    logOut() {
      axios.post("/api/logout").then((response) => {
        console.log("signed out!!!");
        this.logged = false;
      });
    },
    createAccount() {
      axios
        .post(
          "/api/clients/current/accounts",
          `typeAccount=${this.typeAccount}`
        )
        .then((response) => {
          this.loadData();
          this.typeAccountModalClose();
        })
        .catch((err) => console.log(err));
    },
    deleteAccount(accountNumber) {
      axios
        .put("/api/clients/current/accounts", `accountNumber=${accountNumber}`)
        .then((response) => {
          console.log("borrada");
          this.closeModal(accountNumber)
          this.deletAccountOk();
        })
        .catch(err => {

          console.log(err.response.data);
          this.closeModal(accountNumber);
          this.deleteAccountError();

        });
    },

    downloadResumePDF() {
      const { id, startDate, endDate } = this.transferObject;
      const url = `/api/downloadPDF?id=${id}&startDate=${startDate}&endDate=${endDate}`;

      axios.post(url, null, {
        headers: {
          'Accept': 'application/pdf', // Agregamos el encabezado "Accept" para indicar que queremos un archivo PDF en la respuesta
        },
        responseType: 'blob', // Indicamos que esperamos una respuesta en formato Blob
      })
        .then(response => {
          console.log("pdffdf");

          const blob = new Blob([response.data], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'Account_Resume.pdf';
          a.click();
        })
        .catch(err => console.log(err.response.data))
    },

    /* COSAS */
    deletAccountOk() {
      document.getElementById("deletAccountOk").style.display = "block";
    },
    deleteAccountError() {
      document.getElementById("deleteAccountError").style.display = "block"
    },
    closeDeleteAccountError() {
      document.getElementById("deleteAccountError").style.display = "none"
    },

    closeModal() {
      document.getElementById("confirmationModal").style.display = "none";
    },
    typeAccountModal() {
      document.getElementById("typeAcc").style.display = "block";
    },
    typeAccountModalClose() {
      document.getElementById("typeAcc").style.display = "none";
    },

    missingDataModal() {
      document.getElementById("missingDataModal").style.display = "block";
    },
    closeMissingDataModal() {
      document.getElementById("missingDataModal").style.display = "none";
    },


    confirmationModal(accountNumber) {
      const modalNumber = `confirmationModal-${accountNumber}`;
      const modal = document.getElementById(modalNumber);
      console.log(modalNumber);
      modal.style.display = 'block';
    },
    closeModal() {
      document.getElementById("confirmationModal").style.display = "none";
    },
    closeModal(accountNumber) {
      const modalNumber = `confirmationModal-${accountNumber}`;
      const modal = document.getElementById(modalNumber);
      modal.style.display = 'none';
    },

  },
});

app.mount("#app");
