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

            clientLoanName: "",
            clientLoanPayment: [],
            clientLoans: [],
            totalToPay: 0,
            finalNumber: 0,
            feeToPay: 0,

            /*v-models*/

            transferObject: {},
            clientLoanId: "",
            numberAccount: "",
            description: "",
            payment: 0,

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
                    this.accounts.forEach(acc => {
                        acc.balance = this.amountFormat.format(acc.balance);
                    });
                    this.loans = this.client.loans
                    console.log(this.loans);
                    this.logged = true;
                    /*                     console.log(this.accounts);*/

                    this.clientLoanId = new URLSearchParams(location.search).get("id");
                    this.clientLoanName = new URLSearchParams(location.search).get("name");
                    this.clientLoanPayment = new URLSearchParams(location.search).get("payment");


                    this.clientLoans = this.loans.find(loan => {
                        return loan.id == this.clientLoanId
                    })
                    console.log(this.clientLoans);


                })
                .catch((err) => console.error());


        },

        payLoans() {
            this.transferObject = {
                id: this.clientLoanId,
                numberAccount: this.numberAccount,
                description: this.description,
                payment: this.payment

            };
            console.log(this.transferObject);
            axios
                .post("/api/loans/payments", this.transferObject)
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

        /* cosas */

        missingData() {
            if (
                this.clientLoanId == "" ||
                this.totalToPay == 0.0 ||
                this.payment == 0 ||
                this.numberAccount == "" ||
                this.description == ""
            ) {
                this.missingDataModal();
            } else {
                this.confirmationModal();
            }
        },
        logOut() {
            axios.post("/api/logout").then((response) => {
                console.log("signed out!!!");
                this.logged = false;
            });
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
        calculateFees() {
            this.feeToPay = this.clientLoans.amount * (1 + this.clientLoans.percentage / 100) / this.clientLoanPayment;

            this.totalToPay = this.feeToPay * this.payment

            this.finalNumber = parseInt(this.totalToPay).toFixed(2)

            console.log(this.feeToPay);

            console.log(this.totalToPay);

        },
        formatUSD() {
            this.amountFormat = new Intl.NumberFormat("en-US", {
                style: "currency",
                currency: "USD",
            });
            this.amountFormated = this.amountFormat.format(this.feeToPay);
            this.amountFormated = this.amountFormat.format(this.totalToPay);

        },
    },
});

app.mount("#app");
