@param java.util.List<ru.mentee.power.crm.domain.Lead> leads
@param ru.mentee.power.crm.domain.LeadStatus statusFilter

@template.layout.main(content = @`
    <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-bold mb-4">Lead List</h2>

        <div class="mb-4 flex gap-2">
            <a href="/leads"
               class="px-4 py-2 rounded ${statusFilter == null ? "bg-blue-500 text-white" : "bg-gray-200"}">
                Все
            </a>
            <a href="/leads?status=NEW"
               class="px-4 py-2 rounded ${statusFilter != null && statusFilter.name().equals("NEW") ? "bg-blue-500 text-white" : "bg-gray-200"}">
                NEW
            </a>
            <a href="/leads?status=CONTACTED"
               class="px-4 py-2 rounded ${statusFilter != null && statusFilter.name().equals("CONTACTED") ? "bg-blue-500 text-white" : "bg-gray-200"}">
                CONTACTED
            </a>
            <a href="/leads?status=QUALIFIED"
               class="px-4 py-2 rounded ${statusFilter != null && statusFilter.name().equals("QUALIFIED") ? "bg-blue-500 text-white" : "bg-gray-200"}">
                QUALIFIED
            </a>
            <a href="/leads?status=LOST"
               class="px-4 py-2 rounded ${statusFilter != null && statusFilter.name().equals("LOST") ? "bg-blue-500 text-white" : "bg-gray-200"}">
                LOST
            </a>
        </div>

        <div class="mb-4 flex justify-between items-center">
            <a
                    href="/leads/new"
                    class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
            >
                + Добавить лида
            </a>
        </div>

        @if(statusFilter != null)
            <p class="text-sm text-gray-600 mb-2">Показаны лиды со статусом: ${statusFilter}</p>
        @endif

        <table class="min-w-full bg-white border border-gray-200">
            <thead class="bg-gray-100">
            <tr>
                <th class="px-4 py-2 text-left">Name</th>
                <th class="px-4 py-2 text-left">Email</th>
                <th class="px-4 py-2 text-left">Company</th>
                <th class="px-4 py-2 text-left">Status</th>
                <th class="px-4 py-2 text-left">Actions</th>
            </tr>
            </thead>
            <tbody>
            @for(var lead : leads)
                <tr class="border-t hover:bg-gray-50">
                    <td class="px-4 py-2">${lead.contact().name()}</td>
                    <td class="px-4 py-2">${lead.contact().email()}</td>
                    <td class="px-4 py-2">${lead.company()}</td>
                    <td class="px-4 py-2">
                            <span class="px-2 py-1 rounded text-sm bg-green-100 text-green-800">
                                ${lead.status()}
                            </span>
                    </td>
                    <td class="px-4 py-2">
                        <a href="/leads/${lead.id().toString()}/edit" class="text-blue-600 hover:underline">
                            Редактировать
                        </a>
                        <form method="post" action="/leads/${lead.id().toString()}/delete" class="inline" onclick="return confirm('Удалить лида ${lead.contact().name()}?')">
                            <button type="submit" class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700">
                                Удалить
                            </button>
                        </form>
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`)