@param java.util.List<ru.mentee.power.crm.domain.Lead> leads
@param ru.mentee.power.crm.domain.LeadStatus currentStatusFilter
@param java.util.List<ru.mentee.power.crm.domain.LeadIndustry> industries
@param ru.mentee.power.crm.domain.LeadIndustry currentIndustryFilter

@template.layout.main(content = @`
    <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-bold mb-4">Lead List</h2>

        <div class="mb-4 flex gap-2">
            <a href="/leads"
               class="px-4 py-2 rounded ${currentStatusFilter == null ? "bg-blue-500 text-white" : "bg-gray-200"}">
                Все
            </a>
            <a href="/leads?status=NEW"
               class="px-4 py-2 rounded ${currentStatusFilter != null && currentStatusFilter.name().equals("NEW") ? "bg-blue-500 text-white" : "bg-gray-200"}">
                NEW
            </a>
            <a href="/leads?status=CONTACTED"
               class="px-4 py-2 rounded ${currentStatusFilter != null && currentStatusFilter.name().equals("CONTACTED") ? "bg-blue-500 text-white" : "bg-gray-200"}">
                CONTACTED
            </a>
            <a href="/leads?status=QUALIFIED"
               class="px-4 py-2 rounded ${currentStatusFilter != null && currentStatusFilter.name().equals("QUALIFIED") ? "bg-blue-500 text-white" : "bg-gray-200"}">
                QUALIFIED
            </a>
        </div>

        <div class="mb-4 flex gap-4 items-center">
            <form action="/leads" method="get" class="flex gap-2 items-center">
                <label for="industry" class="text-sm font-medium text-gray-700">Отрасль:</label>
                <select
                    id="industry"
                    name="industry"
                    onchange="this.form.submit()"
                    class="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                >
                    <option value="">Все отрасли</option>
                    @for(var industry : industries)
                        @if(currentIndustryFilter != null && currentIndustryFilter.equals(industry))
                            <option value="${industry}" selected>
                                ${industry}
                            </option>
                        @else
                            <option value="${industry}">
                                ${industry}
                            </option>
                        @endif
                    @endfor
                </select>
                @if(currentStatusFilter != null)
                    <input type="hidden" name="status" value="${currentStatusFilter}"/>
                @endif
            </form>
        </div>

        <div class="mb-4 flex justify-between items-center">
            <a
                    href="/leads/new"
                    class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
            >
                + Добавить лида
            </a>
        </div>

        @if(currentStatusFilter != null)
            <p class="text-sm text-gray-600 mb-2">Показаны лиды со статусом: ${currentStatusFilter}</p>
        @endif
        @if(currentIndustryFilter != null)
            <p class="text-sm text-gray-600 mb-2">Показаны лиды отрасли: ${currentIndustryFilter}</p>
        @endif

        <table class="min-w-full bg-white border border-gray-200">
            <thead class="bg-gray-100">
            <tr>
                <th class="px-4 py-2 text-left">Email</th>
                <th class="px-4 py-2 text-left">Company</th>
                <th class="px-4 py-2 text-left">Status</th>
                <th class="px-4 py-2 text-left">Industry</th>
            </tr>
            </thead>
            <tbody>
            @for(var lead : leads)
                <tr class="border-t hover:bg-gray-50">
                    <td class="px-4 py-2">${lead.contact().email()}</td>
                    <td class="px-4 py-2">${lead.company()}</td>
                    <td class="px-4 py-2">
                            <span class="px-2 py-1 rounded text-sm bg-green-100 text-green-800">
                                ${lead.status()}
                            </span>
                    </td>
                    <td class="px-4 py-2">${lead.industry()}</td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`)
