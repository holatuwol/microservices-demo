/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.serializer;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.SystemFDSEntryRegistry;
import com.liferay.frontend.data.set.action.FDSBulkActions;
import com.liferay.frontend.data.set.action.FDSBulkActionsRegistry;
import com.liferay.frontend.data.set.action.FDSCreationMenu;
import com.liferay.frontend.data.set.action.FDSCreationMenuRegistry;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.action.FDSItemsActionsRegistry;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilterContextContributor;
import com.liferay.frontend.data.set.filter.FDSFilterContextContributorRegistry;
import com.liferay.frontend.data.set.filter.FDSFilterRegistry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
@Component(
	property = "frontend.data.set.serializer.type=" + FDSSerializer.TYPE_SYSTEM,
	service = FDSSerializer.class
)
public class SystemFDSSerializer
	extends BaseFDSSerializer implements FDSSerializer {

	@Override
	public String serializeAPIURL(
		String fdsName, HttpServletRequest httpServletRequest) {

		SystemFDSEntry systemFDSEntry =
			_systemFDSEntryRegistry.getSystemFDSEntry(fdsName);

		if (systemFDSEntry == null) {
			return null;
		}

		return createFDSAPIURLBuilder(
			httpServletRequest, systemFDSEntry.getRESTApplication(),
			systemFDSEntry.getRESTEndpoint(), systemFDSEntry.getRESTSchema()
		).addQueryString(
			systemFDSEntry.getAdditionalAPIURLParameters()
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> serializeBulkActions(
		String fdsName, HttpServletRequest httpServletRequest) {

		FDSBulkActions fdsBulkActions =
			_fdsBulkActionsRegistry.getFDSBulkActions(fdsName);

		if (fdsBulkActions == null) {
			return Collections.emptyList();
		}

		return fdsBulkActions.getFDSActionDropdownItems(httpServletRequest);
	}

	@Override
	public CreationMenu serializeCreationMenu(
		String fdsName, HttpServletRequest httpServletRequest) {

		FDSCreationMenu fdsCreationMenu =
			_fdsCreationMenuRegistry.getFDSCreationMenu(fdsName);

		if (fdsCreationMenu == null) {
			return new CreationMenu();
		}

		return fdsCreationMenu.getCreationMenu(httpServletRequest);
	}

	@Override
	public JSONArray serializeFilters(
		String fdsName, HttpServletRequest httpServletRequest) {

		return serializeFilters(
			fdsName, Collections.emptyList(), httpServletRequest);
	}

	@Override
	public JSONArray serializeFilters(
		String fdsName, List<FDSFilter> fdsFilters,
		HttpServletRequest httpServletRequest) {

		Locale locale = _portal.getLocale(httpServletRequest);

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		_serializeFilters(fdsFilters, jsonArray, locale);
		_serializeFilters(
			_fdsFilterRegistry.getFDSFilters(fdsName), jsonArray, locale);

		return jsonArray;
	}

	@Override
	public List<FDSActionDropdownItem> serializeItemsActions(
		String fdsName, HttpServletRequest httpServletRequest) {

		FDSItemsActions fdsItemsActions =
			_fdsItemsActionsRegistry.getFDSItemsActions(fdsName);

		if (fdsItemsActions == null) {
			return Collections.emptyList();
		}

		return fdsItemsActions.getFDSActionDropdownItems(httpServletRequest);
	}

	private void _serializeFilters(
		List<FDSFilter> fdsFilters, JSONArray jsonArray, Locale locale) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		for (FDSFilter fdsFilter : fdsFilters) {
			if (!fdsFilter.isEnabled()) {
				continue;
			}

			JSONObject jsonObject = JSONUtil.put(
				"entityFieldType", fdsFilter.getEntityFieldType()
			).put(
				"id", fdsFilter.getId()
			).put(
				"label", _language.get(resourceBundle, fdsFilter.getLabel())
			).put(
				"preloadedData", fdsFilter.getPreloadedData()
			).put(
				"type", fdsFilter.getType()
			);

			List<FDSFilterContextContributor> fdsFilterContextContributors =
				_fdsFilterContextContributorRegistry.
					getFDSFilterContextContributors(fdsFilter.getType());

			for (FDSFilterContextContributor fdsFilterContextContributor :
					fdsFilterContextContributors) {

				Map<String, Object> fdsFilterContext =
					fdsFilterContextContributor.getFDSFilterContext(
						fdsFilter, locale);

				if (fdsFilterContext == null) {
					continue;
				}

				for (Map.Entry<String, Object> entry :
						fdsFilterContext.entrySet()) {

					jsonObject.put(entry.getKey(), entry.getValue());
				}
			}

			jsonArray.put(jsonObject);
		}
	}

	@Reference
	private FDSBulkActionsRegistry _fdsBulkActionsRegistry;

	@Reference
	private FDSCreationMenuRegistry _fdsCreationMenuRegistry;

	@Reference
	private FDSFilterContextContributorRegistry
		_fdsFilterContextContributorRegistry;

	@Reference
	private FDSFilterRegistry _fdsFilterRegistry;

	@Reference
	private FDSItemsActionsRegistry _fdsItemsActionsRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SystemFDSEntryRegistry _systemFDSEntryRegistry;

}