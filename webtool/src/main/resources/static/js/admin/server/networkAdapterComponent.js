Vue.component('adapter-details-row', {
    props: ['adapter', 'edit', 'index'],
    template: `<tr>
                    <td>
                        <span>{{adapter.name}}</span>
                    </td>
                    <td>
                        <span>{{adapter.ipAddress}}</span>
                    </td>
                    <td>
                        <span>{{adapter.subnetMask}}</span>
                    </td>
                    <td>
                        <span>{{adapter.defaultGateway}}</span>
                    </td>
                    <td>
                        <span>{{adapter.dns}}</span>
                    </td>
                    <td v-if="!edit">
                        <a :id="'button-adapter-row-edit-' + index" class="btn btn-light"
                        style="width: 100px"
                        v-on:click.prevent="handleChange()" href="#">
                        Change</a>
                    </td>
               </tr>`,
    methods: {
        handleChange: function() {
            this.$root.$emit('emitChangeAdapter', this.adapter)
        }
    }
});
