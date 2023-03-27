Vue.component('ntp-server-row', {
    props: ['server', 'edit', 'index'],
    template: `<tr>
                    <td>
                        <span v-show="!edit">{{server.type}}</span>
                        <select v-show="edit"
                            :id="'input-server-type-' + index" v-model="server.type"
                            name="template" class="form-select" aria-label="select server type">
                            <option value="SERVER" :selected="server.type === 'SERVER'">SERVER</option>
                            <option value="POOL" :selected="server.type === 'POOL'">POOL</option>
                        </select>
                    </td>
                    <td>
                        <span v-show="!edit">{{server.address}}</span>
                        <input v-show="edit"
                        :id="'input-server-address-' + index"
                        v-model="server.address" :maxlength="255"
                        class="form-control">
                    </td>
                    <td v-if="edit">
                        <a :id="'button-server-row-delete-' + index" class="btn btn-light"
                        style="width: 100px"
                        v-on:click.prevent="handleDelete()" href="#">
                        Delete</a>
                    </td>
               </tr>`,
    methods: {
        handleDelete: function() {
            this.$root.$emit('emitDeleteNtpServer', this.index)
        }
    }
});
