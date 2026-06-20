@param ru.mentee.power.crm.domain.Lead lead

@template.layout.main(
  content = @`
    <div class="max-w-md mx-auto mt-8">
      <h1 class="text-2xl font-bold mb-6">Редактирование лида</h1>

      <form method="post" action="/leads/${lead.id().toString()}" class="bg-white p-6 rounded shadow">
        <input type="hidden" name="id" value="${lead.id().toString()}">

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="name">
            Имя
          </label>
          <input
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="name"
            type="text"
            name="name"
            value="${lead.contact().name()}"
            required
          />
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="email">
            Email
          </label>
          <input
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="email"
            type="email"
            name="email"
            value="${lead.contact().email()}"
            required
          />
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="phone">
            Телефон
          </label>
          <input
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="phone"
            type="tel"
            name="phone"
            value="${lead.contact().phone()}"
            required
          />
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="city">
            Город
          </label>
          <input
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="city"
            type="text"
            name="city"
            value="${lead.contact().address().city()}"
            required
          />
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="street">
            Улица
          </label>
          <input
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="street"
            type="text"
            name="street"
            value="${lead.contact().address().street()}"
            required
          />
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="zip">
            Индекс
          </label>
          <input
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="zip"
            type="text"
            name="zip"
            value="${lead.contact().address().zip()}"
            required
          />
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="company">
            Компания
          </label>
          <input
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="company"
            type="text"
            name="company"
            value="${lead.company()}"
            required
          />
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="status">
            Статус
          </label>
          <select
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="status"
            name="status"
            required
          >
            <option value="NEW" selected="${lead.status().name().equals("NEW")}">NEW</option>
            <option value="CONTACTED" selected="${lead.status().name().equals("CONTACTED")}">CONTACTED</option>
            <option value="QUALIFIED" selected="${lead.status().name().equals("QUALIFIED")}">QUALIFIED</option>
            <option value="LOST" selected="${lead.status().name().equals("LOST")}">LOST</option>
          </select>
        </div>

        <button
          type="submit"
          class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500 mt-6"
        >
          Сохранить изменения
        </button>

        <a
          href="/leads"
          class="block text-center text-sm text-gray-600 hover:text-gray-900 mt-2"
        >
          Отмена
        </a>
      </form>
    </div>
  `
)
