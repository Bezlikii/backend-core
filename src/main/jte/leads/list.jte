@param java.util.List<ru.mentee.power.crm.domain.Lead> leads
@param String search
@param String status

@template.layout.main(content = @`
    <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-bold mb-4">Lead List</h2>

        <form method="get" action="/leads" class="bg-gray-50 border border-gray-200 rounded-lg p-4 mb-6 flex flex-wrap gap-3 items-center">
            <input type="text" name="search" value="${search != null ? search : ""}"
                   placeholder="Поиск по имени или email..."
                   class="flex-1 min-w-[200px] px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            <select name="status"
                    class="px-3 py-2 border border-gray-300 rounded-md bg-white focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="" selected="${status == null || status.isEmpty()}">Все статусы</option>
                <option value="NEW" selected="${status != null && status.equals("NEW")}">NEW</option>
                <option value="CONTACTED" selected="${status != null && status.equals("CONTACTED")}">CONTACTED</option>
                <option value="QUALIFIED" selected="${status != null && status.equals("QUALIFIED")}">QUALIFIED</option>
                <option value="LOST" selected="${status != null && status.equals("LOST")}">LOST</option>
            </select>
            <button type="submit" class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
                Найти
            </button>
            <a href="/leads" class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300">
                Сбросить
            </a>
        </form>

        <div class="mb-4 flex justify-between items-center">
            <a
                    href="/leads/new"
                    class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
            >
                + Добавить лида
            </a>
        </div>

        @if(status != null && !status.isEmpty())
            <p class="text-sm text-gray-600 mb-2">Показаны лиды со статусом: ${status}</p>
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
                        <div class="flex items-center gap-3">
                            <a href="/leads/${lead.id().toString()}/edit" 
                               class="inline-flex items-center px-3 py-1.5 text-sm font-medium text-blue-700 bg-blue-50 border border-blue-200 rounded-md hover:bg-blue-100 hover:border-blue-300 transition-colors">
                                Редактировать
                            </a>
                            <form method="post" action="/leads/${lead.id().toString()}/delete" 
                                  class="inline" 
                                  onclick="return confirm('Удалить лида ${lead.contact().name()}?')">
                                <button type="submit" 
                                        class="inline-flex items-center px-3 py-1.5 text-sm font-medium text-white bg-red-600 border border-red-600 rounded-md hover:bg-red-700 transition-colors">
                                    Удалить
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`)