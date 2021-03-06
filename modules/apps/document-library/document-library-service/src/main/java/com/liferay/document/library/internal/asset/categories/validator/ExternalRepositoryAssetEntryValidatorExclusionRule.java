/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.document.library.internal.asset.categories.validator;

import com.liferay.asset.kernel.validator.AssetEntryValidatorExclusionRule;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	immediate = true,
	property = {
		"model.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
		"model.class.name=com.liferay.portal.kernel.repository.model.FileEntry"
	},
	service = AssetEntryValidatorExclusionRule.class
)
public class ExternalRepositoryAssetEntryValidatorExclusionRule
	implements AssetEntryValidatorExclusionRule {

	@Override
	public boolean isValidationExcluded(
		long groupId, String className, long classPK, long classTypePK,
		long[] categoryIds, String[] tagNames) {

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
			classPK);

		if (dlFileEntry == null) {
			try {
				DLFileVersion dlFileVersion =
					_dlFileVersionLocalService.fetchDLFileVersion(classPK);

				dlFileEntry = dlFileVersion.getFileEntry();
			}
			catch (PortalException pe) {
				if (_log.isDebugEnabled()) {
					_log.debug(pe, pe);
				}
			}
		}

		if ((dlFileEntry == null) ||
			(dlFileEntry.getRepositoryId() != groupId)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExternalRepositoryAssetEntryValidatorExclusionRule.class);

	@Reference(unbind = "-")
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference(unbind = "-")
	private DLFileVersionLocalService _dlFileVersionLocalService;

}