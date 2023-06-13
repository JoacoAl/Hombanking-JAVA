const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      clients: [],
      restResponse: [],
      clientData: {
        firstName: "",
        lastName: "",
        email: "",
      },
    };
  },

  created() {
    this.loadData();
  },

  methods: {
    loadData() {
      axios
        .get("http://localhost:8080/rest/clients")
        .then((response) => {
          console.log(response);
          this.clients = response.data._embedded.clients;
          console.log(this.clients);
          this.restResponse = response.data;
          console.log(this.clients);
        })
        .catch((err) => console.log(err));
    },
    addClient() {
      this.postClient();
    },
    postClient() {
      axios
        .post("http://localhost:8080/rest/clients", this.clientData)
        .then((res) => {
          console.log(res);
          this.loadData();
          this.borrarInputs();
        })
        .catch((err) => console.log(err));
    },
    borrarInputs() {
      this.clientData.firstName = "";
      this.clientData.lastName = "";
      this.clientData.email = "";
    },
    deleteClients(clientId) {
      axios.delete(clientId).then((res) => {
        this.loadData();
      });
    },
  },
});

app.mount("#app");
