const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      password: "",
      email: "",
      firstName: "",
      lastName: "",
      parameters: [],
    };
  },

  created() {
    this.parameters = new URLSearchParams(location.search).get("login"); //te devuelve los parametros de la url
  },
  methods: {
    login() {
      if (this.email != "" && this.password != "") {
        this.signin();
      }
    },
    signin() {
      axios
        .post("/api/login", `email=${this.email}&password=${this.password}`, {
          headers: { "content-type": "application/x-www-form-urlencoded" },
        })
        .then((response) => {
          console.log("signed in!!!");
          this.password = "";
          this.email = "";
          window.location.href = "/web/index.html";
        })
        .catch((err) => {
          this.invalidDataModal();
        });
    },
    register() {
      axios
        .post(
          "/api/clients",
          `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`,
          { headers: { "content-type": "application/x-www-form-urlencoded" } }
        )
        .then((response) => {
          this.signin();
        })
        .catch((err) => {
          this.errorModalRegister();
        });
    },
    /* modal */
    errorModalRegister() {
      document.getElementById("errorModalRegister").style.display = "block";
    },
    closeErrorModalRegister() {
      document.getElementById("errorModalRegister").style.display = "none";
    },
    /* 1 */
    invalidDataModal() {
      document.getElementById("invalidDataModal").style.display = "block";
    },
    closeInvalidDataModal() {
      document.getElementById("invalidDataModal").style.display = "none";
    },
    /* 2 */
    preventDefault() {
      event.preventDefault();
    },
  },
});

app.mount("#app");
